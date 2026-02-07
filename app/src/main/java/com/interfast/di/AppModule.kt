package com.interfast.di

import android.content.Context
import androidx.room.Room
import com.interfast.data.db.FastSessionDao
import com.interfast.data.db.InterfastDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InterfastDatabase {
        return Room.databaseBuilder(
            context,
            InterfastDatabase::class.java,
            InterfastDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFastSessionDao(database: InterfastDatabase): FastSessionDao {
        return database.fastSessionDao()
    }
}
