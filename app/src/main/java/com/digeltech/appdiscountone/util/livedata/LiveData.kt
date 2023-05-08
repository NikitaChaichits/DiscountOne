package com.digeltech.appdiscountone.util.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<Event<T>>.postEventValue(value: T) {
    this.postValue(Event(value))
}

fun <T> MutableLiveData<Event<T>>.setEventValue(value: T) {
    this.value = Event(value)
}

fun <T> LiveData<T>.toMutable() = convert<MutableLiveData<T>>()

inline fun <reified T> LiveData<*>.convert(): T {

    return this as? T ?: throw ClassCastException("${this::class} is not ${T::class}")
}