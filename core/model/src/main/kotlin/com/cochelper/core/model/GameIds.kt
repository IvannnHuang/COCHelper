package com.cochelper.core.model

import kotlinx.serialization.Serializable

/**
 * Name → index for each category. The index is the id used in army share links, and the
 * data export's global id is `categoryPrefix + index` (see PLAN.md §5.2).
 */
@Serializable
data class GameIds(
    val troops: Map<String, Int>,
    val spells: Map<String, Int>,
    val heroes: Map<String, Int>,
)
