package com.cochelper.feature.attack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cochelper.core.ui.PlaceholderScreen

@Composable
internal fun AttackScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = "Attack plan",
        milestone = "Coming in M5",
        description = "The recommended army with an Equip button, plus step-by-step deployment hints.",
        modifier = modifier,
    )
}
