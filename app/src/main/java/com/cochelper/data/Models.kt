package com.cochelper.data

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
    val troops: List<Unit> = emptyList(),
    val heroes: List<Unit> = emptyList(),
    val spells: List<Unit> = emptyList(),
    val heroEquipment: List<Unit> = emptyList(),
)

@Serializable
data class Unit(
    val name: String,
    val level: Int,
    val maxLevel: Int,
    val village: String = "home",
)

@Serializable
data class ApiError(val reason: String? = null, val message: String? = null)

enum class LayoutPurpose { WAR, FARMING, TROPHY, CWL }

/** A base layout shared through the game's official "Copy layout" link. */
@Serializable
data class Layout(
    val name: String,
    val townHall: Int,
    val purpose: LayoutPurpose,
    val link: String,
    val rating: Int = 3,
    val notes: String = "",
)

enum class StrategyType { AIR, GROUND, HYBRID }

/** An attack plan: what to bring and how to deploy it. `{side}` in steps is replaced with the chosen side. */
@Serializable
data class Strategy(
    val id: String,
    val name: String,
    val type: StrategyType,
    val minTh: Int,
    val maxTh: Int,
    val troops: Map<String, Int>,
    val spells: Map<String, Int>,
    val steps: List<String>,
)

/** Maps unit names to the numeric ids used in army share links. */
@Serializable
data class UnitIds(
    val troops: Map<String, Int>,
    val spells: Map<String, Int>,
)
