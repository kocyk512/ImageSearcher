package com.krzysztofkocot.imagesearcher.ui.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BluetoothOnOffReceiver() : BroadcastReceiver() {

    var onStateOn: () -> Unit = {}

    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        // When discovery finds a device
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            when (state) {
                BluetoothAdapter.STATE_OFF -> Log.d("KK", "onReceive: STATE OFF")
                BluetoothAdapter.STATE_TURNING_OFF -> Log.d(
                    "KK",
                    "mBroadcastReceiver1: STATE TURNING OFF"
                )
                BluetoothAdapter.STATE_ON -> onStateOn()
                BluetoothAdapter.STATE_TURNING_ON -> Log.d(
                    "KK",
                    "mBroadcastReceiver1: STATE TURNING ON"
                )
            }
        }
    }
}