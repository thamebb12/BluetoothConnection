package com.thame.test

import com.thame.test.data.BluetoothDevice
import com.thame.test.data.Message
import kotlinx.coroutines.flow.StateFlow

expect class BluetoothManager(){
    fun startScan() : List<BluetoothDevice>
}
