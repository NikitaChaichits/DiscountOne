package com.digeltech.appdiscountone.data.util

import com.digeltech.appdiscountone.util.log

inline fun tryCatch(body: () -> Unit): Boolean {
    return try {
        body()
        true
    } catch (e: Throwable) {
        log(e.message)
        false
    }
}