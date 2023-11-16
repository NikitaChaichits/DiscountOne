package com.digeltech.discountone.ui.auth.forgot

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    fun resetPassword(email: String) {
        viewModelScope.launchWithLoading {
            authRepository.resetPasswordFirstStep(email)
                .onSuccess {
                    success.postValue(true)
                }
                .onFailure {
                    success.postValue(false)
                }
        }
    }
}