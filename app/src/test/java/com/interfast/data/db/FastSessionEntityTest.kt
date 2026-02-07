package com.interfast.data.db

import com.interfast.domain.model.FastSession
import com.interfast.domain.model.FastStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class FastSessionEntityTest {

    @Test
    fun `fromDomain creates entity with correct values`() {
        val session = FastSession(
            id = 42,
            protocolId = "16_8",
            protocolName = "16:8",
            fastingHours = 16,
            eatingHours = 8,
            startedAt = Instant.ofEpochMilli(1000000),
            endedAt = null,
            completedAt = null,
            status = FastStatus.ACTIVE,
            completionPercentage = 0.5f
        )

        val entity = FastSessionEntity.fromDomain(session)

        assertEquals(42L, entity.id)
        assertEquals("16_8", entity.protocolId)
        assertEquals("16:8", entity.protocolName)
        assertEquals(16, entity.fastingHours)
        assertEquals(8, entity.eatingHours)
        assertEquals(1000000L, entity.startedAt)
        assertNull(entity.endedAt)
        assertNull(entity.completedAt)
        assertEquals("ACTIVE", entity.status)
        assertEquals(0.5f, entity.completionPercentage, 0.001f)
    }

    @Test
    fun `toDomain creates session with correct values`() {
        val entity = FastSessionEntity(
            id = 123,
            protocolId = "18_6",
            protocolName = "18:6",
            fastingHours = 18,
            eatingHours = 6,
            startedAt = 2000000L,
            endedAt = 3000000L,
            completedAt = 3000000L,
            status = "COMPLETED",
            completionPercentage = 1.0f
        )

        val session = entity.toDomain()

        assertEquals(123L, session.id)
        assertEquals("18_6", session.protocolId)
        assertEquals("18:6", session.protocolName)
        assertEquals(18, session.fastingHours)
        assertEquals(6, session.eatingHours)
        assertEquals(Instant.ofEpochMilli(2000000), session.startedAt)
        assertEquals(Instant.ofEpochMilli(3000000), session.endedAt)
        assertEquals(Instant.ofEpochMilli(3000000), session.completedAt)
        assertEquals(FastStatus.COMPLETED, session.status)
        assertEquals(1.0f, session.completionPercentage, 0.001f)
    }

    @Test
    fun `roundtrip preserves all data`() {
        val original = FastSession(
            id = 99,
            protocolId = "custom_20_4",
            protocolName = "20:4",
            fastingHours = 20,
            eatingHours = 4,
            startedAt = Instant.ofEpochMilli(5000000),
            endedAt = Instant.ofEpochMilli(6000000),
            completedAt = null,
            status = FastStatus.CANCELLED,
            completionPercentage = 0.75f
        )

        val entity = FastSessionEntity.fromDomain(original)
        val restored = entity.toDomain()

        assertEquals(original.id, restored.id)
        assertEquals(original.protocolId, restored.protocolId)
        assertEquals(original.protocolName, restored.protocolName)
        assertEquals(original.fastingHours, restored.fastingHours)
        assertEquals(original.eatingHours, restored.eatingHours)
        assertEquals(original.startedAt, restored.startedAt)
        assertEquals(original.endedAt, restored.endedAt)
        assertEquals(original.completedAt, restored.completedAt)
        assertEquals(original.status, restored.status)
        assertEquals(original.completionPercentage, restored.completionPercentage, 0.001f)
    }

    @Test
    fun `toDomain handles null timestamps`() {
        val entity = FastSessionEntity(
            id = 1,
            protocolId = "16_8",
            protocolName = "16:8",
            fastingHours = 16,
            eatingHours = 8,
            startedAt = 1000000L,
            endedAt = null,
            completedAt = null,
            status = "ACTIVE",
            completionPercentage = 0f
        )

        val session = entity.toDomain()

        assertNull(session.endedAt)
        assertNull(session.completedAt)
    }
}
