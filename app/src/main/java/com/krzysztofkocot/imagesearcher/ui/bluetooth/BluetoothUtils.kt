package com.krzysztofkocot.imagesearcher.ui.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.os.Build
import android.util.Log
import androidx.fragment.app.FragmentActivity

fun checkBTPermissions(activity: FragmentActivity) {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        var permissionCheck =
            (activity).checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION")
        permissionCheck += (activity).checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
        if (permissionCheck != 0) {
            activity.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1001
            )
        }
    } else {
        Log.d("KK", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.")
    }
}

fun         discover(bluetoothAdapter: BluetoothAdapter) {
    Log.d("KK", "discover(): Looking for unpaired devices.")
    bluetoothAdapter.apply {
        if (isDiscovering) {
            cancelDiscovery()
            Log.d("KK", "discover(): Canceling discovery.")
            startDiscovery()
        }
        if (!isDiscovering) {
            Log.d("KK", "discover(): Discovering.")
            startDiscovery()
        }
    }
}