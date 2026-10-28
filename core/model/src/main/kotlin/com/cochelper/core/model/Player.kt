package com.cochelper.core.model

import kotlinx.serialization.Serializable

/** Subset of the official API's GET /players/{tag} response. */
@Serializable
data class Player(
    val tag: String,
    val name: String,
    val townHallLevel: Int,
    val expLevel: Int = 0,
    val trophies: Int = 0,
    val warStars: Int = 0,
    val clan: ClanRef? = null,
    val troops: List<PlayerUnit> = emptyList(),
    val heroes: List<PlayerUnit> = emptyList(),
    val spells: List<PlayerUnit> = emptyList(),
    val heroEquipment: List<PlayerUnit> = emptyList(),
)

@Serializable
data class ClanRef(
    val tag: String,
    val name: String,
    val clanLevel: Int = 0,
)

/**
 * A troop, hero, spell or equipment entry. `maxLevel` is the overall game max, not the Town Hall cap.
 * `village` is "home" or "builderBase".
 */
@Serializable
data class PlayerUnit(
    val name: String,
    val level: Int,
    val maxLevel: Int,
    val village: String = "home",
)
