package com.digeltech.discountone.data.util

import com.digeltech.discountone.util.log

inline fun tryCatch(body: () -> Unit): Boolean {
    return try {
        body()
        true
    } catch (e: Throwable) {
        log(e.message)
        false
    }
}