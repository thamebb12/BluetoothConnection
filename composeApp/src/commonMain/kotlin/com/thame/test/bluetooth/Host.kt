package com.thame.test.bluetooth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.thame.test.BluetoothManager
import com.thame.test.data.BluetoothDevice
import com.thame.test.navController.Controller
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Host(
    navController: NavController
) {

    val bluetoothManager = remember { BluetoothManager() }
//    var discoveredDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val discoveredDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val isScanning = remember { mutableStateOf(false) }

    LaunchedEffect(isScanning.value) {
        while (isScanning.value) {
            delay(2000) // Refresh every 2 seconds
            val newDevices = bluetoothManager.startScan()
            discoveredDevices.clear()
            discoveredDevices.addAll(newDevices)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween, // Spreads elements evenly from top to bottom
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Section (Status Text)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isScanning.value) {
                Text("Starting...", textAlign = TextAlign.Center, fontSize = 18.sp)
                Text("Scanning for devices...", textAlign = TextAlign.Center, fontSize = 16.sp)
            }
        }

        // Middle Section (List of Discovered Devices)
        Box(
            modifier = Modifier
                .weight(1f, fill = true) // Fills available space
                .fillMaxWidth()
        ) {
            if (discoveredDevices.isEmpty()) {
                Text(
                    "No devices found",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(discoveredDevices) { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            elevation = 4.dp,
                            onClick = {
                                device.identifier?.let { bluetoothManager.connect(it) }
                                navController.navigate(Controller.Chat.name)
                            }
                        ) {
                            Text(
                                text = "${device.name ?: "Unknown"} - ${device.identifier}",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Section (Buttons)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    isScanning.value = true
                    bluetoothManager.startScan()
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Start Scanning")
            }

            Button(
                onClick = { navController.navigate(Controller.Home.name) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Go Back")
            }
        }
    }
}

