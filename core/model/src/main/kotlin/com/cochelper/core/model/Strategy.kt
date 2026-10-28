package com.cochelper.core.model

import kotlinx.serialization.Serializable

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
