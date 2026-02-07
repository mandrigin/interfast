package com.interfast.data.repository

import com.interfast.data.db.FastSessionDao
import com.interfast.data.db.FastSessionEntity
import com.interfast.data.preferences.UserPreferences
import com.interfast.domain.model.DailyFastingSummary
import com.interfast.domain.model.DailyStatus
import com.interfast.domain.model.FastSession
import com.interfast.domain.model.FastStatus
import com.interfast.domain.model.FastingProtocol
import com.interfast.domain.model.FastingStats
import com.interfast.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FastingRepository @Inject constructor(
    private val dao: FastSessionDao,
    private val preferences: UserPreferences
) {
    // Active session
    fun observeActiveSession(): Flow<FastSession?> {
        return dao.observeActiveSession().map { it?.toDomain() }
    }

    suspend fun getActiveSession(): FastSession? {
        return dao.getActiveSession()?.toDomain()
    }

    // Start a new fast
    suspend fun startFast(protocol: FastingProtocol): FastSession {
        // Cancel any existing active session first
        dao.getActiveSession()?.let { active ->
            val updated = active.copy(
                status = FastStatus.CANCELLED.name,
                endedAt = Instant.now().toEpochMilli(),
                completionPercentage = active.toDomain().calculateProgress()
            )
            dao.update(updated)
        }

        val session = FastSession(
            protocolId = protocol.id,
            protocolName = protocol.name,
            fastingHours = protocol.fastingHours,
            eatingHours = protocol.eatingHours,
            startedAt = Instant.now(),
            status = FastStatus.ACTIVE
        )

        val id = dao.insert(FastSessionEntity.fromDomain(session))
        return session.copy(id = id)
    }

    // End fast early
    suspend fun endFast(sessionId: Long, completed: Boolean = false): FastSession? {
        val entity = dao.getById(sessionId) ?: return null
        val session = entity.toDomain()
        val now = Instant.now()

        val updatedEntity = entity.copy(
            status = if (completed) FastStatus.COMPLETED.name else FastStatus.CANCELLED.name,
            endedAt = if (!completed) now.toEpochMilli() else null,
            completedAt = if (completed) now.toEpochMilli() else null,
            completionPercentage = session.calculateProgress(now)
        )

        dao.update(updatedEntity)
        return updatedEntity.toDomain()
    }

    // Complete fast (called when timer reaches 100%)
    suspend fun completeFast(sessionId: Long): FastSession? {
        return endFast(sessionId, completed = true)
    }

    // History
    fun observeAllSessions(): Flow<List<FastSession>> {
        return dao.observeAllSessions().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun observeRecentSessions(limit: Int = 10): Flow<List<FastSession>> {
        return dao.observeRecentSessions(limit).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getSessionsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<FastSession>> {
        val zone = ZoneId.systemDefault()
        val startMillis = startDate.atStartOfDay(zone).toInstant().toEpochMilli()
        val endMillis = endDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()

        return dao.getSessionsInRange(startMillis, endMillis).map { list ->
            list.map { it.toDomain() }
        }
    }

    // Stats
    fun observeStats(): Flow<FastingStats> {
        return combine(
            dao.observeCompletedCount(),
            dao.observeTotalCount(),
            dao.observeTotalFastedMillis(),
            dao.observeAllSessions()
        ) { completed, total, totalMillis, sessions ->
            val totalDuration = Duration.ofMillis(totalMillis ?: 0)
            val streak = calculateStreak(sessions.map { it.toDomain() })
            val longestStreak = calculateLongestStreak(sessions.map { it.toDomain() })
            val weeklyStats = calculateWeeklyStats(sessions.map { it.toDomain() })

            FastingStats(
                currentStreak = streak,
                longestStreak = longestStreak,
                totalFasts = total,
                completedFasts = completed,
                totalHoursFasted = totalDuration,
                weeklyAverageHours = weeklyStats.first,
                weeklyCompletionRate = weeklyStats.second
            )
        }
    }

    private fun calculateStreak(sessions: List<FastSession>): Int {
        if (sessions.isEmpty()) return 0

        val completedSessions = sessions
            .filter { it.status == FastStatus.COMPLETED }
            .sortedByDescending { it.completedAt }

        if (completedSessions.isEmpty()) return 0

        var streak = 0
        var currentDate = LocalDate.now()
        val zone = ZoneId.systemDefault()

        // Group sessions by date
        val sessionsByDate = completedSessions.groupBy { session ->
            session.completedAt?.atZone(zone)?.toLocalDate()
        }

        // Check if there's a completed fast today or yesterday (allow 1 day grace)
        val hasRecentFast = sessionsByDate.containsKey(currentDate) ||
                sessionsByDate.containsKey(currentDate.minusDays(1))

        if (!hasRecentFast) return 0

        // Start from the most recent completed fast date
        if (!sessionsByDate.containsKey(currentDate)) {
            currentDate = currentDate.minusDays(1)
        }

        while (sessionsByDate.containsKey(currentDate)) {
            streak++
            currentDate = currentDate.minusDays(1)
        }

        return streak
    }

    private fun calculateLongestStreak(sessions: List<FastSession>): Int {
        val completedSessions = sessions
            .filter { it.status == FastStatus.COMPLETED }
            .mapNotNull { it.completedAt }
            .map { it.atZone(ZoneId.systemDefault()).toLocalDate() }
            .distinct()
            .sorted()

        if (completedSessions.isEmpty()) return 0

        var longestStreak = 1
        var currentStreak = 1

        for (i in 1 until completedSessions.size) {
            val daysBetween = ChronoUnit.DAYS.between(completedSessions[i - 1], completedSessions[i])
            if (daysBetween == 1L) {
                currentStreak++
                longestStreak = maxOf(longestStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }

        return longestStreak
    }

    private fun calculateWeeklyStats(sessions: List<FastSession>): Pair<Float, Float> {
        val weekAgo = Instant.now().minus(7, ChronoUnit.DAYS)
        val weeklySessions = sessions.filter { it.startedAt.isAfter(weekAgo) }

        if (weeklySessions.isEmpty()) return 0f to 0f

        val totalHours = weeklySessions.sumOf { session ->
            session.actualDuration.toHours().toInt()
        }
        val completedCount = weeklySessions.count { it.status == FastStatus.COMPLETED }

        val avgHours = totalHours.toFloat() / 7f
        val completionRate = completedCount.toFloat() / weeklySessions.size

        return avgHours to completionRate
    }

    // Daily summaries for calendar
    fun observeDailySummaries(month: LocalDate): Flow<List<DailyFastingSummary>> {
        val startOfMonth = month.withDayOfMonth(1)
        val endOfMonth = month.withDayOfMonth(month.lengthOfMonth())

        return getSessionsForDateRange(startOfMonth, endOfMonth).map { sessions ->
            val zone = ZoneId.systemDefault()
            val sessionsByDate = sessions.groupBy { session ->
                session.startedAt.atZone(zone).toLocalDate()
            }

            val today = LocalDate.now()
            (1..month.lengthOfMonth()).map { day ->
                val date = month.withDayOfMonth(day)
                val daySessions = sessionsByDate[date] ?: emptyList()

                val status = when {
                    date.isAfter(today) -> DailyStatus.FUTURE
                    daySessions.isEmpty() -> DailyStatus.MISSED
                    daySessions.any { it.status == FastStatus.COMPLETED } -> DailyStatus.COMPLETE
                    else -> DailyStatus.PARTIAL
                }

                val completionPct = daySessions
                    .maxOfOrNull { it.completionPercentage }
                    ?: 0f

                DailyFastingSummary(
                    date = date,
                    status = status,
                    completionPercentage = completionPct,
                    sessions = daySessions
                )
            }
        }
    }

    // Protocol management
    fun observeSelectedProtocolId(): Flow<String> = preferences.selectedProtocolId

    suspend fun getSelectedProtocol(): FastingProtocol {
        val protocolId = preferences.selectedProtocolId.first()
        return FastingProtocol.DEFAULT_PROTOCOLS.find { it.id == protocolId }
            ?: if (protocolId.startsWith("custom_")) {
                val fastingHours = preferences.customFastingHours.first()
                val eatingHours = preferences.customEatingHours.first()
                FastingProtocol.custom(fastingHours, eatingHours)
            } else {
                FastingProtocol.PROTOCOL_16_8
            }
    }

    suspend fun setSelectedProtocol(protocol: FastingProtocol) {
        preferences.setSelectedProtocol(protocol.id)
        if (protocol.isCustom) {
            preferences.setCustomProtocol(protocol.fastingHours, protocol.eatingHours)
        }
    }
}
