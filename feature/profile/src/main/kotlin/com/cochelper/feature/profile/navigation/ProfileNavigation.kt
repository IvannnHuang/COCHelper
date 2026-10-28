package com.cochelper.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cochelper.feature.profile.ProfileScreen
import com.cochelper.feature.profile.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings() = navigate(SettingsRoute) { launchSingleTop = true }

fun NavGraphBuilder.profileScreen(onEditSettings: () -> Unit) {
    composable<ProfileRoute> { ProfileScreen(onEditSettings) }
}

fun NavGraphBuilder.settingsScreen(onDone: () -> Unit) {
    composable<SettingsRoute> { SettingsScreen(onDone) }
}
