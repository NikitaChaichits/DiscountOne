package com.digeltech.discountone.ui.auth.newaccount

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.AuthRepository
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