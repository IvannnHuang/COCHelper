package com.cochelper.feature.scout.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cochelper.feature.scout.ScoutScreen
import kotlinx.serialization.Serializable

@Serializable
data object ScoutRoute

fun NavGraphBuilder.scoutScreen() {
    composable<ScoutRoute> { ScoutScreen() }
}
