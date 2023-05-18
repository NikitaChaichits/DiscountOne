package com.digeltech.appdiscountone.util.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

inline fun <reified T> LiveData<*>.convert(): T {
    return this as? T ?: throw ClassCastException("${this::class} is not ${T::class}")
}

fun <T> MutableLiveData<Event<T>>.postEventValue(value: T) {
    this.postValue(Event(value))
}

fun <T> MutableLiveData<Event<T>>.setEventValue(value: T) {
    this.value = Event(value)
}

fun <T> LiveData<T>.toMutable() = convert<MutableLiveData<T>>()

fun <T> LiveData<T>.observeOnce(observer: (T) -> Unit) {
    observeForever(object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}

fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, object : Observer<T> {
        override fun onChanged(value: T) {
            removeObserver(this)
            observer(value)
        }
    })
}