package com.cochelper.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.cochelper.feature.profile.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

fun NavGraphBuilder.profileScreen() {
    composable<ProfileRoute> { ProfileScreen() }
}
