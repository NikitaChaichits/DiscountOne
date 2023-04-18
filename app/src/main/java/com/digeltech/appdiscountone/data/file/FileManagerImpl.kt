package com.digeltech.appdiscountone.data.file

import android.content.Context
import java.io.File
import javax.inject.Inject

/**
 * Папка с файлами для mockoon
 */
const val DIRECTORY_CACHE = "cache"

class FileManagerImpl @Inject constructor(
    private val context: Context,
) : FileManager {

    override fun getInternalDir(): File = context.applicationContext.filesDir

    override fun getCacheDir(): File = File(getInternalDir(), DIRECTORY_CACHE)
}