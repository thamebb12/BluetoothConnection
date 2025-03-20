package com.thame.test.data

import com.juul.kable.Identifier

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
data class BluetoothDevice(
    val name: String?,
//    val identifier: String?,
    val identifier: Identifier,
    val rssi: Int
)