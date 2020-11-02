package com.krzysztofkocot.imagesearcher.data.bluetooth

import android.bluetooth.BluetoothDevice

data class BluetoothDeviceDomain(val device: BluetoothDevice){
    val address: String = device.address
    val name:String? = device.name
    val alias: String? = device.alias
    val type: Int = device.type
    var isPaired: Boolean = false

    val description: String
        get() = buildString {
            add(name)
            add(alias)
            append(address)
        }

    private fun StringBuilder.add(string: String?) = string?.let {
        append(it)
        append(SEPARATOR)
    }
}

private const val SEPARATOR = " - "
