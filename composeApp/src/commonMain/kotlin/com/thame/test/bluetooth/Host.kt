package com.thame.test.bluetooth

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.logs.Hex
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import com.juul.kable.peripheral
import com.thame.test.data.BluetoothDevice
import com.thame.test.navController.Controller
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Host(
    navController: NavController
) {
//    var discoveredDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val scope = rememberCoroutineScope()
    val scanner = Scanner()
    val discoveredDevices = remember { mutableStateListOf<BluetoothDevice>() }
    val isScanning = remember { mutableStateOf(false) }
    val connectionState = remember { mutableStateOf<State?>(null) }

    LaunchedEffect(isScanning.value) {
        if (isScanning.value) {
            scanner.advertisements.collect { advertisement ->
                val deviceName = advertisement.name
                if (deviceName != null) {
                    val device = BluetoothDevice(
                        name = deviceName,
                        identifier = advertisement.identifier,
                        rssi = advertisement.rssi ?: 0
                    )
                    if (discoveredDevices.none { it.identifier == device.identifier }) {
                        discoveredDevices.add(device)
                    }
                }
            }
            val advertisement = Scanner {
                filters {
                    match {
                        name = Filter.Name.Exact("iPad")
                    }
                }
            }.advertisements.first()
        }
    }



    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = {
            discoveredDevices.clear()
            isScanning.value = true
        }) {
            Text("Start Scanning")
        }

        LazyColumn {
            items(discoveredDevices) { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = {

                        scope.launch {
                            val advertisement = Scanner {
                                filters {
                                    match {
                                        name = Filter.Name.Exact("Example")
                                    }
                                }
                            }.advertisements.first()

                            val peripheral = Peripheral(advertisement) {
                                logging {
                                    engine = SystemLogEngine
                                    level = Logging.Level.Warnings
                                    format = Logging.Format.Multiline
                                    data = Hex
                                }
                            }

                            try {
                                peripheral.connect()
                                connectionState.value = peripheral.state.value
                                println("Connected to ${device.name}")
                                navController.navigate(Controller.Chat.name)
                            } catch (e: Exception) {
                                println("Connection failed: ${e.message}")
                            }
                        }
                    }
                ) {
                    Text(
                        "${device.name} - ${device.identifier}",
                        modifier = Modifier.padding(16.dp)
                    )
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

