package com.krzysztofkocot.imagesearcher.ui.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay
import com.krzysztofkocot.imagesearcher.put
import com.krzysztofkocot.imagesearcher.ui.utils.log

class BluetoothBondReceiver : BroadcastReceiver() {

    val deviceBondingS = BehaviorRelay.createDefault(false)
    val deviceHasBeenBondedS = PublishRelay.create<Unit>()
//    val bondedDeviceS : BehaviorRelay<Optional<BluetoothDevice>> = BehaviorRelay.createDefault(null.optional)

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        Log.d("KK", "onReceive: ACTION FOUND.")

        if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            when (device?.bondState) {
                BluetoothDevice.BOND_NONE -> {
                    log("BOND_NONE")
                    deviceBondingS put false
//                    bondedDeviceS put null.optional
                }
                BluetoothDevice.BOND_BONDING -> {
                    log("BOND_BONDING")
                    deviceBondingS put true
//                    bondedDeviceS put null.optional
                }
                BluetoothDevice.BOND_BONDED -> {
                    log("BOND_BONDED")
                    deviceBondingS put false
                    deviceHasBeenBondedS put Unit
//                    bondedDeviceS put device.optional
                }
            }
        }
    }
}

