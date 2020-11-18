package com.krzysztofkocot.imagesearcher.ui.dialog

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.krzysztofkocot.imagesearcher.R

fun showDevicesDialog(ctx: Context, items: Array<String>, onItemClick: (Int) -> Unit, onCloseClick: (Int) -> Unit) =
    AlertDialog.Builder(ctx)
        .setTitle(ctx.getString(R.string.choose_device))
        .setSingleChoiceItems(
            items,
            NO_DEVICE_IS_CHECKED
        ) { _, which -> onItemClick(which) }
        .setNeutralButton("Close") { _, _ -> }
        .create()
        .show()

const val NO_DEVICE_IS_CHECKED = -1