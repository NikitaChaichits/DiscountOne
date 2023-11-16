package com.digeltech.discountone.ui.auth.recovery

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecoveryPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    fun resetPassword(userId: String, password: String) {
        viewModelScope.launchWithLoading {
            authRepository.resetPasswordSecondStep(userId, password)
                .onSuccess {
                    success.postValue(true)
                }
        }
    }
}