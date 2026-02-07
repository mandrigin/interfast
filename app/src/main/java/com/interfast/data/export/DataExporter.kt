package com.interfast.data.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.interfast.data.repository.FastingRepository
import com.interfast.domain.model.FastSession
import com.interfast.domain.model.FastStatus
import com.interfast.domain.model.FastingStats
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data exporter for user data portability.
 *
 * Privacy-first design: Users own their data and should be able to:
 * 1. Export it for backup
 * 2. Import it to a new device
 * 3. Analyze it in spreadsheets
 * 4. Delete it completely
 *
 * Formats supported:
 * - JSON: Complete data for backup/restore
 * - CSV: For spreadsheet analysis
 */
@Singleton
class DataExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: FastingRepository
) {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
    }

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    private val fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        .withZone(ZoneId.systemDefault())

    /**
     * Export all data as JSON.
     *
     * @return Uri to the exported file, ready for sharing
     */
    suspend fun exportToJson(): ExportResult {
        return try {
            val sessions = repository.observeAllSessions().first()
            val stats = repository.observeStats().first()

            val exportData = ExportData(
                exportedAt = Instant.now().toString(),
                appVersion = getAppVersion(),
                sessions = sessions.map { it.toExportSession() },
                statistics = stats.toExportStats()
            )

            val jsonContent = json.encodeToString(exportData)
            val file = createExportFile("json")
            file.writeText(jsonContent)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            ExportResult.Success(uri, file.name, "application/json")
        } catch (e: Exception) {
            ExportResult.Error("Failed to export data: ${e.message}")
        }
    }

    /**
     * Export session history as CSV.
     *
     * @return Uri to the exported file, ready for sharing
     */
    suspend fun exportToCsv(): ExportResult {
        return try {
            val sessions = repository.observeAllSessions().first()

            val csvContent = buildString {
                // Header
                appendLine("id,protocol_name,fasting_hours,eating_hours,started_at,ended_at,completed_at,status,completion_percentage,duration_hours")

                // Data rows
                sessions.forEach { session ->
                    val startedAt = dateFormatter.format(session.startedAt)
                    val endedAt = session.endedAt?.let { dateFormatter.format(it) } ?: ""
                    val completedAt = session.completedAt?.let { dateFormatter.format(it) } ?: ""
                    val durationHours = session.actualDuration.toHours()

                    appendLine(
                        "${session.id}," +
                        "\"${session.protocolName}\"," +
                        "${session.fastingHours}," +
                        "${session.eatingHours}," +
                        "\"$startedAt\"," +
                        "\"$endedAt\"," +
                        "\"$completedAt\"," +
                        "${session.status.name}," +
                        "${session.completionPercentage}," +
                        "$durationHours"
                    )
                }
            }

            val file = createExportFile("csv")
            file.writeText(csvContent)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            ExportResult.Success(uri, file.name, "text/csv")
        } catch (e: Exception) {
            ExportResult.Error("Failed to export data: ${e.message}")
        }
    }

    /**
     * Create a share intent for the exported file.
     */
    fun createShareIntent(result: ExportResult.Success): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = result.mimeType
            putExtra(Intent.EXTRA_STREAM, result.uri)
            putExtra(Intent.EXTRA_SUBJECT, "Interfast Data Export")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun createExportFile(extension: String): File {
        val exportDir = File(context.cacheDir, "exports").apply {
            if (!exists()) mkdirs()
        }

        val timestamp = fileNameFormatter.format(Instant.now())
        return File(exportDir, "interfast_export_$timestamp.$extension")
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun FastSession.toExportSession() = ExportSession(
        id = id,
        protocolId = protocolId,
        protocolName = protocolName,
        fastingHours = fastingHours,
        eatingHours = eatingHours,
        startedAt = startedAt.toString(),
        endedAt = endedAt?.toString(),
        completedAt = completedAt?.toString(),
        status = status.name,
        completionPercentage = completionPercentage,
        durationMinutes = actualDuration.toMinutes()
    )

    private fun FastingStats.toExportStats() = ExportStats(
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        totalFasts = totalFasts,
        completedFasts = completedFasts,
        totalHoursFasted = totalHoursFasted.toHours(),
        weeklyAverageHours = weeklyAverageHours,
        weeklyCompletionRate = weeklyCompletionRate
    )
}

sealed class ExportResult {
    data class Success(
        val uri: Uri,
        val fileName: String,
        val mimeType: String
    ) : ExportResult()

    data class Error(val message: String) : ExportResult()
}

@Serializable
data class ExportData(
    val exportedAt: String,
    val appVersion: String,
    val sessions: List<ExportSession>,
    val statistics: ExportStats
)

@Serializable
data class ExportSession(
    val id: Long,
    val protocolId: String,
    val protocolName: String,
    val fastingHours: Int,
    val eatingHours: Int,
    val startedAt: String,
    val endedAt: String?,
    val completedAt: String?,
    val status: String,
    val completionPercentage: Float,
    val durationMinutes: Long
)

@Serializable
data class ExportStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalFasts: Int,
    val completedFasts: Int,
    val totalHoursFasted: Long,
    val weeklyAverageHours: Float,
    val weeklyCompletionRate: Float
)
