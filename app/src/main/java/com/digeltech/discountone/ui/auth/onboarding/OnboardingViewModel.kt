package com.digeltech.discountone.ui.auth.onboarding

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.Gender
import com.digeltech.discountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    fun updateProfileWithAvatar(
        id: String,
        birthday: String?,
        gender: Gender?,
        userAvatar: MultipartBody.Part?
    ) {
        viewModelScope.launchWithLoading {
            authRepository.updateProfileWithAvatar(
                id = id,
                birthday = birthday,
                login = null,
                gender = gender?.name,
                userAvatar = userAvatar
            ).onSuccess {
                success.postValue(true)
            }.onFailure {
                error.postValue(it.toString())
            }
        }
    }

    fun updateProfile(
        id: String,
        birthday: String?,
        gender: Gender?,
    ) {
        viewModelScope.launchWithLoading {
            authRepository.updateProfile(
                id = id,
                login = null,
                birthday = birthday,
                gender = gender?.name,

                ).onSuccess {
                success.postValue(true)
            }.onFailure {
                error.postValue(it.toString())
            }
        }
    }
}