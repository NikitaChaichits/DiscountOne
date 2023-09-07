package com.digeltech.discountone.util

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun getInternalDir(context: Context): File = context.applicationContext.filesDir

fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, fileName: String) {
    val directory = context.filesDir
    val imageFile = File(directory, fileName)

    try {
        val fos = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
    } catch (e: IOException) {
        log(e)
    }
}