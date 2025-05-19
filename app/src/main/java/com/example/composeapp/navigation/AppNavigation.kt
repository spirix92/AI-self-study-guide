package com.example.composeapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api

sealed class Screen(val route: String, val title: String) {
    object Splash : Screen("splash", "Загрузка")
    object Home : Screen("home", "Главная")
    object Profile : Screen("profile", "Профиль")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentScreen = when (currentRoute) {
        Screen.Home.route -> Screen.Home
        Screen.Profile.route -> Screen.Profile
        Screen.Splash.route -> Screen.Splash
        else -> Screen.Home
    }
    val showBars = currentScreen != Screen.Splash

    Scaffold(
        topBar = {
            if (showBars) {
                TopBar(
                    title = currentScreen.title,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    onBack = { navController.navigateUp() }
                )
            }
        },
        bottomBar = {
            if (showBars) {
                BottomBar(currentScreen) { screen ->
                    if (screen.route != currentRoute) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) { SplashScreen(navController) }
            composable(Screen.Home.route) { SimpleScreen(Screen.Home.title) }
            composable(Screen.Profile.route) { SimpleScreen(Screen.Profile.title) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(title: String, canNavigateBack: Boolean, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
fun BottomBar(currentScreen: Screen, onTabSelected: (Screen) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = currentScreen == Screen.Home,
            onClick = { onTabSelected(Screen.Home) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text(Screen.Home.title) }
        )
        NavigationBarItem(
            selected = currentScreen == Screen.Profile,
            onClick = { onTabSelected(Screen.Profile) },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text(Screen.Profile.title) }
        )
    }
}

@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(1000)
        navController.navigate(Screen.Home.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun SimpleScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = text, modifier = Modifier.align(Alignment.Center))
    }
}

