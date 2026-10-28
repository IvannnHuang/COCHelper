package com.cochelper.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cochelper.feature.attack.navigation.attackScreen
import com.cochelper.feature.layouts.navigation.layoutsScreen
import com.cochelper.feature.profile.navigation.ProfileRoute
import com.cochelper.feature.profile.navigation.profileScreen
import com.cochelper.feature.scout.navigation.scoutScreen
import com.cochelper.feature.upgrades.navigation.UpgradesRoute
import com.cochelper.feature.upgrades.navigation.upgradesScreen
import com.cochelper.navigation.TopLevelDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocHelperApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COC Helper") },
                actions = {
                    IconButton(onClick = { navController.navigate(ProfileRoute) { launchSingleTop = true } }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Account")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                TopLevelDestination.entries.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.hasRoute(destination.routeClass) } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = UpgradesRoute,
            modifier = Modifier.padding(padding),
        ) {
            upgradesScreen()
            layoutsScreen()
            scoutScreen()
            attackScreen()
            profileScreen()
        }
    }
}
