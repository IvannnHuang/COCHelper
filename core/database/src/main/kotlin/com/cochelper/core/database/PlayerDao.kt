package com.cochelper.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * FROM player WHERE tag = :tag")
    fun observe(tag: String): Flow<PlayerEntity?>

    @Query("SELECT * FROM player WHERE tag = :tag")
    suspend fun get(tag: String): PlayerEntity?

    @Upsert
    suspend fun upsert(player: PlayerEntity)
}
