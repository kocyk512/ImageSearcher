package com.krzysztofkocot.imagesearcher.data.bluetooth

data class BluetoothDeviceDomain(
    val address: String,
    val name: String,
    val alias: String,
    val type: String,
    val uuids: String
)