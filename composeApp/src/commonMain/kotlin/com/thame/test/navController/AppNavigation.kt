package com.thame.test.navController

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.thame.test.bluetooth.Chat
import com.thame.test.bluetooth.Home
import com.thame.test.bluetooth.Host


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Controller.Home.name) {
        composable(Controller.Home.name) { Home(navController) }
        composable(Controller.Host.name) { Host(navController) }
        composable(Controller.Chat.name) { Chat(navController) }
    }
}