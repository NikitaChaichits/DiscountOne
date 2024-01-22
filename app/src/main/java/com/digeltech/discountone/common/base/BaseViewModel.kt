package com.digeltech.discountone.common.base

import androidx.annotation.MainThread
import androidx.collection.ArraySet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.digeltech.discountone.ui.common.model.DealParcelable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel : ViewModel() {

    val loading = MutableSharedFlow<Boolean>(replay = 1)
    val error = MutableLiveData<String>()
    val success = MutableLiveData<Boolean>()
    val searchResult = MutableLiveData<List<DealParcelable>>()

    var searchJob: Job? = null
    var filteringJob: Job? = null

    val loadingGifVisibility = MutableLiveData(false)

    suspend fun MutableSharedFlow<Boolean>.start() = this.emit(true)

    suspend fun MutableSharedFlow<Boolean>.stop() = this.emit(false)

    fun CoroutineScope.launchWithLoading(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return launch(context, start) {
            loading.start()
            block()
            delay(2000)
            loading.stop()
        }
    }

    fun stopJobs() {
        if (filteringJob?.isActive == true) filteringJob?.cancel()
        if (searchJob?.isActive == true) searchJob?.cancel()
    }

    @MainThread
    fun <T> State<T>.setValueNow(newValue: T) = internalSetValueNow(newValue)
    fun <T> State<T>.setValue(newValue: T) = internalPostValue(newValue)

    @MainThread
    fun <T> Event<T>.callNow(newValue: T) = internalCall(newValue)
    fun <T> Event<T>.call(newValue: T) = internalPostCall(newValue)
    fun Event<Unit>.call() = internalPostCall(Unit)

    class State<T>(initValue: T? = null) {

        private var _liveData = MutableLiveData<T>()

        init {
            initValue?.let { _liveData.value = it }
        }

        val liveData: LiveData<T> get() = _liveData

        val value: T? get() = _liveData.value
        val valueNonNull: T get() = value!!
        val hasValue: Boolean get() = value != null

        @MainThread
        internal fun internalSetValueNow(newValue: T) {
            _liveData.apply { value = newValue }
        }

        internal fun internalPostValue(newValue: T) {
            _liveData.apply { postValue(newValue) }
        }

        internal fun setLiveData(newLiveData: MutableLiveData<T>) {
            _liveData = newLiveData
        }
    }

    class Event<T> {

        private val _liveData = LiveEvent<T>()

        val liveData: LiveData<T> get() = _liveData
        val value: T? get() = _liveData.value

        @MainThread
        internal fun internalCall(newValue: T) {
            _liveData.apply { value = newValue }
        }

        internal fun internalPostCall(newValue: T) {
            _liveData.apply { postValue(newValue) }
        }
    }
}

open class LiveEvent<T> : MediatorLiveData<T>() {

    private val observers = ArraySet<ObserverWrapper<T>>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer)
        observers.add(wrapper)
        super.observe(owner, wrapper)
    }

    @MainThread
    override fun observeForever(observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer)
        observers.add(wrapper)
        super.observeForever(wrapper)
    }

    @MainThread
    override fun removeObserver(observer: Observer<in T>) {
        if (observers.remove(observer as Observer<*>)) {
            super.removeObserver(observer)
            return
        }

        val iterator = observers.iterator()
        while (iterator.hasNext()) {
            val wrapper = iterator.next()
            if (wrapper.observer == observer) {
                iterator.remove()
                super.removeObserver(wrapper)
                break
            }
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        observers.forEach { it.newValue() }
        super.setValue(t)
    }

    private class ObserverWrapper<T>(val observer: Observer<in T>) : Observer<T> {

        private var pending = false

        override fun onChanged(t: T) {
            if (pending) {
                pending = false
                observer.onChanged(t)
            }
        }

        fun newValue() {
            pending = true
        }
    }
}
