package com.thame.test.bluetooth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.thame.test.navController.Controller

@Composable
fun Home(
    navController: NavController = rememberNavController()
){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween, // Spreads elements evenly from top to bottom
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate(Controller.Host.name) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Go to Host")
            }

            Button(
                onClick = { navController.navigate(Controller.Chat.name) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Go to Chat")
            }
        }
    }
}