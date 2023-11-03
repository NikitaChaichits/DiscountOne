package com.digeltech.discountone.util.imagepicker

internal interface ResultListener<T> {
    fun onResult(t: T?)
}
