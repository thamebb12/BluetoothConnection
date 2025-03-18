package com.thame.test

import com.thame.test.data.BluetoothDevice
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBManagerStatePoweredOn
import platform.CoreBluetooth.CBPeripheral
import platform.Foundation.NSNumber
import platform.darwin.NSObject

actual class BluetoothManager : NSObject(), CBCentralManagerDelegateProtocol {

    private var centralManager: CBCentralManager = CBCentralManager(delegate = this, queue = null)
    private val peripheralSet = mutableSetOf<CBPeripheral>()
    private val discoveredDevices = mutableListOf<BluetoothDevice>()

    actual fun startScan(): List<BluetoothDevice> {
        centralManager.scanForPeripheralsWithServices(null, options = null)
        return getDiscoveredDevices()
    }

    private fun getDiscoveredDevices(): List<BluetoothDevice> {
        return discoveredDevices
    }

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        when (central.state) {
            CBManagerStatePoweredOn -> {
                startScan()
            }
            else -> println("Bluetooth not available")
        }
    }


    override fun centralManager(
        central: CBCentralManager,
        didDiscoverPeripheral: CBPeripheral,
        advertisementData: Map<Any?, *>,
        RSSI: NSNumber
    ) {
        val deviceName = didDiscoverPeripheral.name
        if (deviceName != null && peripheralSet.add(didDiscoverPeripheral)) {
            val device = BluetoothDevice(
                name = deviceName,
                identifier = didDiscoverPeripheral.identifier.UUIDString,
                rssi = RSSI.intValue
            )
            discoveredDevices.add(device)
            println("Discovered: ${device.name} - ${device.identifier}")
        }
    }

    override fun centralManager(
        central: CBCentralManager,
        didConnectPeripheral: CBPeripheral
    ) {
        println("Connected to ${didConnectPeripheral.name ?: "Unknown"}")
    }
}


