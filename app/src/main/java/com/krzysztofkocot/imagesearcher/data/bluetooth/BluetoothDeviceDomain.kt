package com.krzysztofkocot.imagesearcher.data.bluetooth

import android.bluetooth.BluetoothDevice

data class BluetoothDeviceDomain(
    val address: String,
    val name: String?,
    val alias: String?,
    val type: Int,
)

fun BluetoothDevice.toDomain() = BluetoothDeviceDomain(
    this.address,
    this.name,
    this.alias,
    this.type,
)
