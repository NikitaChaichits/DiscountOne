package com.digeltech.discountone.ui.auth.onboarding

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    fun updateProfile(id: String, city: String?, birthday: String?) {
        viewModelScope.launchWithLoading {
            authRepository.updateProfile(
                id = id,
                city = city,
                birthday = birthday,
                login = null
            ).onSuccess {
                success.postValue(true)
            }.onFailure {
                error.postValue(it.toString())
            }
        }
    }
}