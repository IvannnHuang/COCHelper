package com.cochelper.feature.layouts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cochelper.core.ui.PlaceholderScreen

@Composable
internal fun LayoutsScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = "Base layouts",
        milestone = "Coming in M3",
        description = "Layouts for your Town Hall. Apply opens the official share link, and the game asks to copy it.",
        modifier = modifier,
    )
}
