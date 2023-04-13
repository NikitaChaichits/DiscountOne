package com.digeltech.appdiscountone.ui.alldeals

import com.digeltech.appdiscountone.common.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DealsViewModel @Inject constructor() : BaseViewModel() {

    private val _text = MutableStateFlow("This is deals Fragment")
    val text: StateFlow<String> = _text.asStateFlow()

}