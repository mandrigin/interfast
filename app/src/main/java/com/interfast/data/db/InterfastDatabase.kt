package com.interfast.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FastSessionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class InterfastDatabase : RoomDatabase() {
    abstract fun fastSessionDao(): FastSessionDao

    companion object {
        const val DATABASE_NAME = "interfast_db"
    }
}
