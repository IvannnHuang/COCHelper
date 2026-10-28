package com.cochelper.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.cochelper.feature.attack.navigation.AttackRoute
import com.cochelper.feature.layouts.navigation.LayoutsRoute
import com.cochelper.feature.scout.navigation.ScoutRoute
import com.cochelper.feature.upgrades.navigation.UpgradesRoute
import kotlin.reflect.KClass

/** The bottom-bar tabs. Profile is reached from the top bar instead. */
enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val route: Any,
    val routeClass: KClass<*>,
) {
    UPGRADES("Upgrades", Icons.Filled.Build, UpgradesRoute, UpgradesRoute::class),
    LAYOUTS("Layouts", Icons.Filled.Home, LayoutsRoute, LayoutsRoute::class),
    SCOUT("Scout", Icons.Filled.Search, ScoutRoute, ScoutRoute::class),
    ATTACK("Attack", Icons.Filled.PlayArrow, AttackRoute, AttackRoute::class),
}
