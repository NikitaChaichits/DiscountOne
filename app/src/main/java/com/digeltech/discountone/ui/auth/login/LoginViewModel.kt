package com.digeltech.discountone.ui.auth.login

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    fun login(email: String, password: String) {
        viewModelScope.launchWithLoading {
            authRepository.login(email, password)
                .onSuccess {
                    success.postValue(true)
                }
                .onFailure {
                    error.postValue(it.message)
                }
        }
    }
}