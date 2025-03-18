package com.thame.test

import com.thame.test.data.BluetoothDevice
import com.thame.test.data.Message
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import platform.CoreBluetooth.CBATTRequest
import platform.CoreBluetooth.CBAttributePermissionsReadable
import platform.CoreBluetooth.CBAttributePermissionsWriteable
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBCentralManagerStatePoweredOff
import platform.CoreBluetooth.CBCentralManagerStatePoweredOn
import platform.CoreBluetooth.CBCentralManagerStateResetting
import platform.CoreBluetooth.CBCentralManagerStateUnauthorized
import platform.CoreBluetooth.CBCentralManagerStateUnknown
import platform.CoreBluetooth.CBCentralManagerStateUnsupported
import platform.CoreBluetooth.CBCharacteristicPropertyNotify
import platform.CoreBluetooth.CBCharacteristicPropertyWriteWithoutResponse
import platform.CoreBluetooth.CBCharacteristicWriteWithoutResponse
import platform.CoreBluetooth.CBManagerStatePoweredOn
import platform.CoreBluetooth.CBMutableCharacteristic
import platform.CoreBluetooth.CBMutableService
import platform.CoreBluetooth.CBPeripheral
import platform.CoreBluetooth.CBPeripheralManager
import platform.CoreBluetooth.CBPeripheralManagerDelegateProtocol
import platform.CoreBluetooth.CBUUID
import platform.Foundation.NSData
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.darwin.NSObject
import platform.darwin.swtch
import platform.posix.memcpy

actual class BluetoothManager : NSObject(), CBCentralManagerDelegateProtocol,
    CBPeripheralManagerDelegateProtocol {

    private var centralManager: CBCentralManager = CBCentralManager(delegate = this, queue = null)
    private var peripheralManager: CBPeripheralManager? = null

    private var transferCharacteristic: CBMutableCharacteristic? = null
    private var connectedPeripheral: CBPeripheral? = null
    private val discoveredDevices = mutableListOf<BluetoothDevice>()
    private val peripheralSet = mutableSetOf<CBPeripheral>()

    // Store received messages
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    actual val messages: StateFlow<List<Message>> get() = _messages

    init {
        setupPeripheral()
        peripheralManager = CBPeripheralManager(delegate = this, queue = null)
    }

    private fun setupPeripheral() {
        val characteristicUUID = CBUUID.UUIDWithString("0000FFE1-0000-1000-8000-00805F9B34FB")
        transferCharacteristic = CBMutableCharacteristic(
            type = characteristicUUID,
            properties = CBCharacteristicPropertyNotify or CBCharacteristicPropertyWriteWithoutResponse,
            value = null,
            permissions = CBAttributePermissionsReadable or CBAttributePermissionsWriteable
        )

        val serviceUUID = CBUUID.UUIDWithString("0000FFE0-0000-1000-8000-00805F9B34FB")
        val transferService = CBMutableService(type = serviceUUID, primary = true).apply {
            setCharacteristics(listOf(transferCharacteristic))
        }

        peripheralManager?.addService(transferService)
    }

    actual fun startScan() : List<BluetoothDevice> {
//        centralManager.scanForPeripheralsWithServices(uuid ,options = null)
        val services: List<CBUUID>? = null // Scan for all peripherals
        centralManager.scanForPeripheralsWithServices(services, options = null)
        return  getDiscoveredDevices()
    }

    private fun getDiscoveredDevices(): List<BluetoothDevice> {
        return discoveredDevices
    }

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        when (central.state) {
            CBCentralManagerStatePoweredOn -> {
                startScan()
            }

            CBCentralManagerStatePoweredOff -> {

            }

            CBCentralManagerStateResetting -> {

            }

            CBCentralManagerStateUnsupported -> {

            }

            CBCentralManagerStateUnauthorized -> {

            }

            CBCentralManagerStateUnknown -> {

            }
        }
    }

    override fun centralManager(
        central: CBCentralManager,
        didDiscoverPeripheral: CBPeripheral,
        advertisementData: Map<Any?, *>,
        RSSI: NSNumber
    ) {

        val deviceName = didDiscoverPeripheral.name
        val device = BluetoothDevice(deviceName, didDiscoverPeripheral.identifier.UUIDString, RSSI.intValue)

//        if (deviceName != null && peripheralSet.add(didDiscoverPeripheral)) {
//            val device = BluetoothDevice(
//                name = deviceName,
//                identifier = didDiscoverPeripheral.identifier.UUIDString,
//                rssi = RSSI.intValue
//            )
//            discoveredDevices.add(device)
//            println("Discovered: ${device.name} - ${device.identifier}")
//        }
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

    actual fun connect(devicesID: String) {
        val devices = discoveredDevices.find { it.identifier == devicesID }
        devices?.let {
            connectedPeripheral?.let { it1 -> centralManager.connectPeripheral(it1, options = null) }
            println("Connecting to ${it.name ?: "Unknown"} - ${it.identifier}")
        }
    }

    override fun centralManager(
        central: CBCentralManager,
        didConnectPeripheral: CBPeripheral
    ) {

        connectedPeripheral = didConnectPeripheral
        println("Connected to ${didConnectPeripheral.name ?: "Unknown"}")
    }

    actual fun sendMessage(message: String) {
        val data = NSString.create(string = message).dataUsingEncoding(NSUTF8StringEncoding)
        if (data != null && connectedPeripheral != null && transferCharacteristic != null) {
            if ((transferCharacteristic!!.properties and CBCharacteristicPropertyWriteWithoutResponse).toInt() != 0) {
                connectedPeripheral!!.writeValue(data, transferCharacteristic!!, CBCharacteristicWriteWithoutResponse)
                println("Sent: $message")
            } else {
                println("Characteristic does not support Write Without Response")
            }
        } else {
            println("Failed to send message: Device not connected or characteristic missing")
        }
    }

//#verion1
//    override fun peripheralManager(
//        peripheral: CBPeripheralManager,
//        didReceiveWriteRequests: List<*>
//    ) {
//        didReceiveWriteRequests.forEach { request ->
//            if (request is CBATTRequest && request.characteristic == transferCharacteristic) {
//                val receivedData = request.value?.let { nsDataToByteArray(it).decodeToString() } ?: ""
//
//                // Update messages when received
//                _messages.update { currentMessages ->
//                    currentMessages + Message(receivedData, isSent = false)
//                }
//
//                println("Received Message: $receivedData")
//            }
//        }
//    }


    override fun peripheralManagerDidUpdateState(peripheral: CBPeripheralManager) {
        when (peripheral.state) {
            CBManagerStatePoweredOn -> {
                val latestMessage = _messages.value.lastOrNull()?.text
                if (latestMessage != null) {
                    sendMessage(latestMessage)
                }
            }
            else -> println("Bluetooth not available")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun nsDataToByteArray(nsData: NSData): ByteArray {
        val length = nsData.length.toInt()
        val byteArray = ByteArray(length)
        nsData.bytes?.let { bytesPointer ->
            byteArray.usePinned { pinnedArray ->
                memcpy(pinnedArray.addressOf(0), bytesPointer, length.convert())
            }
        }
        return byteArray
    }


//    #Version2
    override fun peripheralManager(
        peripheral: CBPeripheralManager,
        didReceiveWriteRequests: List<*>
    ) {
    didReceiveWriteRequests.forEach { request ->
        if (request is CBATTRequest && request.characteristic == transferCharacteristic) {
            val receivedData = request.value?.let { nsDataToByteArray(it).decodeToString() } ?: ""

            if (receivedData == "EOM") {
                println("Full message received: ${_messages.value.joinToString("")}")
            } else {
                _messages.update { currentMessages ->
                    currentMessages + Message(receivedData, isSent = false)
                }
            }

            println("Received Message: $receivedData")
        }
    }
    }


}

