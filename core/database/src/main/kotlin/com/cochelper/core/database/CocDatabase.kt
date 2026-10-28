package com.cochelper.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Database(entities = [PlayerEntity::class], version = 1, exportSchema = false)
abstract class CocDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): CocDatabase =
        Room.databaseBuilder(context, CocDatabase::class.java, "cochelper.db")
            // Only caches live here so far; rebuild instead of migrating until user data is stored.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun playerDao(database: CocDatabase): PlayerDao = database.playerDao()
}
