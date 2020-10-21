package com.krzysztofkocot.imagesearcher.ui.utils

import android.R
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.graphics.drawable.toBitmap
import com.krzysztofkocot.imagesearcher.data.local.PixbayDBItem
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

fun savebyMediaStore(photo: PixbayDBItem, drawable: Drawable, context: Context) {
    val relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + "ImageSearcher"
    val contentValues = ContentValues()
    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, photo.id)
    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, R.attr.mimeType)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
    }
    val resolver = context.contentResolver
    var stream: OutputStream? = null
    var uri: Uri? = null
    val bitmap = drawable.toBitmap()

    try {
        val contentUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        uri = resolver.insert(contentUri, contentValues)
        if (uri == null) {
            throw IOException("Failed to create new MediaStore record.")
        }
        stream = resolver.openOutputStream(uri)
        if (stream == null) {
            throw IOException("Failed to get output stream.")
        }
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)) {
            throw IOException("Failed to save bitmap.")
        }
    } catch (e: IOException) {
        if (uri != null) {
            // Don't leave an orphan entry in the MediaStore
            resolver.delete(uri, null, null)
        }
        throw e
    } finally {
        stream?.flush()
        stream?.close()
    }
}

private fun saveByStream(photo: PixbayDBItem, drawable: Drawable, context: Context) {
    val bitmap = drawable.toBitmap()
    val path = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val dir = File("$path")
    dir.mkdirs()
    val fileName = "${photo.id}.jpg"
    val file = File(dir, fileName)
    val outStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
    outStream.flush()
    outStream.close()
}