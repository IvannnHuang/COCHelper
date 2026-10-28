package com.cochelper.feature.layouts.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cochelper.feature.layouts.LayoutsScreen
import kotlinx.serialization.Serializable

@Serializable
data object LayoutsRoute

fun NavGraphBuilder.layoutsScreen() {
    composable<LayoutsRoute> { LayoutsScreen() }
}
