package com.medacuitysoftware.bluetoothtest.ble

enum class BLEUUID {
    val deviceNameUUID = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb")
    val appearanceUUID = UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb")
    val batteryLevelUUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
    val modelNumberUUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb")
    val serialNumberUUID = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb")
    val firmwareRevisionUUID = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb")
    val hardwareRevisionUUID = UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb")
    val softwareRevisionUUID = UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb")
    val manufacturerNameUUID = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb")
}
