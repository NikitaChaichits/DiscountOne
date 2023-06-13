package com.digeltech.appdiscountone.ui.auth.newaccount

import androidx.lifecycle.viewModelScope
import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewAccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    fun register(login: String, email: String, password: String) {
        viewModelScope.launchWithLoading {
            authRepository.register(login, email, password)
                .onSuccess {
                    success.postValue(true)
                }
                .onFailure {
                    error.postValue(it.toString())
                }
        }
    }
}