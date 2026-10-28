package com.cochelper.core.model

import kotlinx.serialization.Serializable

enum class LayoutPurpose { WAR, CWL, FARMING, TROPHY }

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
