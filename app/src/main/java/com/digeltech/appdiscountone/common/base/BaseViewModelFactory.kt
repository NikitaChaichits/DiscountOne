package com.digeltech.appdiscountone.common.base

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Base class of factory of [ViewModel]s, which must be implemented by all factories of [ViewModel]s.
 */
abstract class BaseViewModelFactory<VM : BaseViewModel>(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        @Suppress("UNCHECKED_CAST")
        return createViewModel(handle) as T
    }

    protected abstract fun createViewModel(handle: SavedStateHandle): VM
}