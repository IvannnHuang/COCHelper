package com.cochelper.feature.upgrades.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cochelper.feature.upgrades.UpgradesScreen
import kotlinx.serialization.Serializable

@Serializable
data object UpgradesRoute

fun NavGraphBuilder.upgradesScreen() {
    composable<UpgradesRoute> { UpgradesScreen() }
}
