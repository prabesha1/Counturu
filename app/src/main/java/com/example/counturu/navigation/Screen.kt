package com.example.counturu.navigation

sealed class Screen(val route: String) {
    object Favs : Screen("favs")
    object Home : Screen("home")
    object Settings : Screen("settings")
}

