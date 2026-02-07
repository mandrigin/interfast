package com.interfast.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.interfast.ui.screens.history.HistoryScreen
import com.interfast.ui.screens.onboarding.OnboardingScreen
import com.interfast.ui.screens.protocol.ProtocolScreen
import com.interfast.ui.screens.settings.SettingsScreen
import com.interfast.ui.screens.stats.StatsScreen
import com.interfast.ui.screens.timer.TimerScreen
import com.interfast.ui.theme.InterfastColors
import com.interfast.ui.theme.InterfastTypography
import com.interfast.ui.theme.Motion

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Timer : Screen(
        route = "timer",
        label = "Timer",
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer
    )

    data object History : Screen(
        route = "history",
        label = "History",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )

    data object Stats : Screen(
        route = "stats",
        label = "Stats",
        selectedIcon = Icons.AutoMirrored.Filled.ShowChart,
        unselectedIcon = Icons.AutoMirrored.Outlined.ShowChart
    )

    data object Settings : Screen(
        route = "settings",
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    data object Protocol : Screen(
        route = "protocol",
        label = "Protocol",
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer
    )

    data object Onboarding : Screen(
        route = "onboarding",
        label = "Onboarding",
        selectedIcon = Icons.Filled.Timer,
        unselectedIcon = Icons.Outlined.Timer
    )
}

val bottomNavItems = listOf(
    Screen.Timer,
    Screen.History,
    Screen.Stats,
    Screen.Settings
)

@Composable
fun InterfastNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            InterfastBottomBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(InterfastColors.VoidBlack)
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Timer.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(Motion.DURATION_STANDARD))
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(Motion.DURATION_STANDARD))
                }
            ) {
                composable(Screen.Timer.route) {
                    TimerScreen(
                        onNavigateToProtocols = {
                            navController.navigate(Screen.Protocol.route)
                        }
                    )
                }

                composable(Screen.History.route) {
                    HistoryScreen()
                }

                composable(Screen.Stats.route) {
                    StatsScreen()
                }

                composable(Screen.Settings.route) {
                    SettingsScreen()
                }

                composable(
                    route = Screen.Protocol.route,
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(Motion.DURATION_STANDARD)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(Motion.DURATION_STANDARD)
                        )
                    }
                ) {
                    ProtocolScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onProtocolSelected = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.Onboarding.route,
                    enterTransition = {
                        fadeIn(animationSpec = tween(Motion.DURATION_STANDARD))
                    },
                    exitTransition = {
                        fadeOut(animationSpec = tween(Motion.DURATION_STANDARD))
                    }
                ) {
                    OnboardingScreen(
                        onComplete = {
                            navController.navigate(Screen.Timer.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InterfastBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar on protocol and onboarding screens
    if (currentDestination?.route == Screen.Protocol.route ||
        currentDestination?.route == Screen.Onboarding.route) {
        return
    }

    NavigationBar(
        containerColor = InterfastColors.Gray10,
        contentColor = InterfastColors.PureWhite
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.label
                    )
                },
                label = {
                    Text(
                        text = screen.label.uppercase(),
                        style = InterfastTypography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = InterfastColors.GlyphRed,
                    selectedTextColor = InterfastColors.GlyphRed,
                    unselectedIconColor = InterfastColors.Gray40,
                    unselectedTextColor = InterfastColors.Gray40,
                    indicatorColor = InterfastColors.Gray15
                )
            )
        }
    }
}
