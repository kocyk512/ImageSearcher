package com.krzysztofkocot.imagesearcher.ui.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jakewharton.rxrelay3.BehaviorRelay
import com.krzysztofkocot.imagesearcher.put

class BluetoothDiscoverReceiver : BroadcastReceiver() {

    private val _devices = mutableListOf<BluetoothDevice>()
    val devicesS = BehaviorRelay.create<List<BluetoothDevice>>()

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        Log.d("KK", "onReceive: ACTION FOUND.")

        if (action == BluetoothDevice.ACTION_FOUND) {
            val device: BluetoothDevice? = intent!!.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            device?.let {
                _devices.add(it)
                devicesS put _devices.distinct()
            }
        }
    }
}