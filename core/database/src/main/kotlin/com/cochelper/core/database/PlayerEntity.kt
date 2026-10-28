package com.cochelper.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Last API response for a player, stored as JSON so model changes don't need migrations. */
@Entity(tableName = "player")
data class PlayerEntity(
    @PrimaryKey val tag: String,
    val json: String,
    val fetchedAtMillis: Long,
)
