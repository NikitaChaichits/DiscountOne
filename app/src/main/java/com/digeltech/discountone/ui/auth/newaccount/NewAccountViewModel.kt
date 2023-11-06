package com.digeltech.discountone.ui.auth.newaccount

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewAccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    val registerError = MutableLiveData<String>()
    val userId = MutableLiveData<String>()

    fun register(nickname: String, email: String, password: String) {
        viewModelScope.launchWithLoading {
            authRepository.register(nickname, email, password)
                .onSuccess(userId::postValue)
                .onFailure {
                    registerError.postValue("Failed to register. Email already exists")
                }
        }
    }
}