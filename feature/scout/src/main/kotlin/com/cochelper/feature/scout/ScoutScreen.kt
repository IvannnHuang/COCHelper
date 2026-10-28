package com.cochelper.feature.scout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cochelper.core.ui.PlaceholderScreen

@Composable
internal fun ScoutScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        title = "Scout",
        milestone = "Coming in M4",
        description = "Pick a screenshot of an enemy base and mark its key defenses to find the weak side.",
        modifier = modifier,
    )
}
