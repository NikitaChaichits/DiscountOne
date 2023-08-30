package com.digeltech.discountone.ui.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    val loginError = MutableLiveData<String>()

    fun login(email: String, password: String) {
        viewModelScope.launchWithLoading {
            authRepository.login(email, password)
                .onSuccess {
                    success.postValue(true)
                }
                .onFailure {
                    loginError.postValue("The password or the email address is incorrect")
                }
        }
    }
}