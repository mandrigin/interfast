package com.interfast.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FastSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: FastSessionEntity): Long

    @Update
    suspend fun update(session: FastSessionEntity)

    @Query("SELECT * FROM fast_sessions WHERE id = :id")
    suspend fun getById(id: Long): FastSessionEntity?

    @Query("SELECT * FROM fast_sessions WHERE status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveSession(): FastSessionEntity?

    @Query("SELECT * FROM fast_sessions WHERE status = 'ACTIVE' LIMIT 1")
    fun observeActiveSession(): Flow<FastSessionEntity?>

    @Query("SELECT * FROM fast_sessions ORDER BY startedAt DESC")
    fun observeAllSessions(): Flow<List<FastSessionEntity>>

    @Query("SELECT * FROM fast_sessions ORDER BY startedAt DESC LIMIT :limit")
    fun observeRecentSessions(limit: Int): Flow<List<FastSessionEntity>>

    @Query("""
        SELECT * FROM fast_sessions
        WHERE startedAt >= :startMillis AND startedAt < :endMillis
        ORDER BY startedAt DESC
    """)
    fun getSessionsInRange(startMillis: Long, endMillis: Long): Flow<List<FastSessionEntity>>

    @Query("SELECT COUNT(*) FROM fast_sessions WHERE status = 'COMPLETED'")
    fun observeCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM fast_sessions")
    fun observeTotalCount(): Flow<Int>

    @Query("""
        SELECT SUM(
            CASE
                WHEN completedAt IS NOT NULL THEN completedAt - startedAt
                WHEN endedAt IS NOT NULL THEN endedAt - startedAt
                ELSE 0
            END
        ) FROM fast_sessions WHERE status IN ('COMPLETED', 'CANCELLED')
    """)
    fun observeTotalFastedMillis(): Flow<Long?>

    @Query("DELETE FROM fast_sessions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM fast_sessions")
    suspend fun deleteAll()
}
