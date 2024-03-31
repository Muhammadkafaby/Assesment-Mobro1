package org.d3if0149.assesment.navigation

sealed class Screen (val route: String) {
    data object Home: Screen("MainScreen")
    data object About: Screen("AboutScreen")
}