package com.digeltech.discountone.data.file

import java.io.File

interface FileManager {

    /**
     * Получить путь к внутренней памяти
     */
    fun getInternalDir(): File

    /**
     * Получить путь к папке с кэшом
     */
    fun getCacheDir(): File
}