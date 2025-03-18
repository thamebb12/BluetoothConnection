package com.thame.test

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.thame.test.navController.AppNavigation

@Composable
fun App() {
    MaterialTheme {
        AppNavigation()
    }
}
