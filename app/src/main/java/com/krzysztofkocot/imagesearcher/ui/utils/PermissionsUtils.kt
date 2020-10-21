package com.krzysztofkocot.imagesearcher.ui.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private fun permissionGrated(context: Context) = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
) == PackageManager.PERMISSION_GRANTED

private fun shouldRequestPermission(activity: Activity) = ActivityCompat.shouldShowRequestPermissionRationale(
    activity,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

private fun requestPermission(activity: Activity) = ActivityCompat.requestPermissions(
    activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
    com.krzysztofkocot.imagesearcher.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
)

fun checkPermissionWRITE_EXTERNAL_STORAGE(context: Context, activity: Activity): Boolean {
    val currentAPIVersion = Build.VERSION.SDK_INT
    return if (currentAPIVersion >= Build.VERSION_CODES.M) {
        if (!permissionGrated(context)) {
            if (shouldRequestPermission(activity))
                showDialogPermissions("External storage", activity) {
                    requestPermission(activity)
                }
            else requestPermission(activity)
            false
        } else true
    } else true
}
