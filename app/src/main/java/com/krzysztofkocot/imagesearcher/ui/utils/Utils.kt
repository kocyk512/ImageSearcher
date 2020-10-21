package com.krzysztofkocot.imagesearcher.ui.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.krzysztofkocot.imagesearcher.viewmodel.QueryEvent
import com.krzysztofkocot.imagesearcher.viewmodel.QueryStatus
import com.google.android.material.snackbar.Snackbar

fun dp(pixels: Int, contextSource: View) = Pixels.dp(pixels, contextSource.context)

fun snackbar(host: View, text: String) = Snackbar.make(
    host,
    text,
    Snackbar.LENGTH_LONG
).show()

fun toast(ctx: Context, text: String) = Toast.makeText(
    ctx,
    text,
    Toast.LENGTH_LONG
).show()

fun handleQueryStatus(ctx: Context?, queryEvent: QueryEvent) {
    when (queryEvent.status) {
        QueryStatus.Valid -> {
        }
        QueryStatus.ErrorToShort -> ctx?.let {
            toast(it, "Query \"${queryEvent.data}\" is too short")
        }

        QueryStatus.ErrorFormat -> ctx?.let {
            toast(it, "Query can contain only letters")
        }
    }
}

fun showDialogPermissions(
    msg: String,
    context: Context,
    onPositiveClick: () -> Unit
) {
    AlertDialog.Builder(context)
        .setCancelable(true)
        .setTitle("Permission necessary")
        .setMessage("$msg permission is necessary")
        .setPositiveButton(
            context.resources.getString(android.R.string.ok)
        ) { _, _ ->
            onPositiveClick()
        }
        .create()
        .show()
}