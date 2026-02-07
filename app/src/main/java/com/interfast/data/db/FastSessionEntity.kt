package com.interfast.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.interfast.domain.model.FastSession
import com.interfast.domain.model.FastStatus
import java.time.Instant

@Entity(tableName = "fast_sessions")
data class FastSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val protocolId: String,
    val protocolName: String,
    val fastingHours: Int,
    val eatingHours: Int,
    val startedAt: Long, // Epoch millis
    val endedAt: Long?, // Epoch millis
    val completedAt: Long?, // Epoch millis
    val status: String,
    val completionPercentage: Float
) {
    fun toDomain(): FastSession {
        return FastSession(
            id = id,
            protocolId = protocolId,
            protocolName = protocolName,
            fastingHours = fastingHours,
            eatingHours = eatingHours,
            startedAt = Instant.ofEpochMilli(startedAt),
            endedAt = endedAt?.let { Instant.ofEpochMilli(it) },
            completedAt = completedAt?.let { Instant.ofEpochMilli(it) },
            status = FastStatus.valueOf(status),
            completionPercentage = completionPercentage
        )
    }

    companion object {
        fun fromDomain(session: FastSession): FastSessionEntity {
            return FastSessionEntity(
                id = session.id,
                protocolId = session.protocolId,
                protocolName = session.protocolName,
                fastingHours = session.fastingHours,
                eatingHours = session.eatingHours,
                startedAt = session.startedAt.toEpochMilli(),
                endedAt = session.endedAt?.toEpochMilli(),
                completedAt = session.completedAt?.toEpochMilli(),
                status = session.status.name,
                completionPercentage = session.completionPercentage
            )
        }
    }
}
