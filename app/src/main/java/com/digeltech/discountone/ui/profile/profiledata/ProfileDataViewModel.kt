package com.digeltech.discountone.ui.profile.profiledata

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.Gender
import com.digeltech.discountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileDataViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel() {

    fun updateProfileWithAvatar(
        id: String,
        nickname: String?,
        birthday: String?,
        gender: Gender?,
        userAvatar: MultipartBody.Part?
    ) {
        viewModelScope.launchWithLoading {
            authRepository.updateProfileWithAvatar(
                id = id,
                login = nickname,
                birthday = birthday,
                userAvatar = userAvatar,
                gender = gender?.name
            ).onSuccess {
                success.postValue(true)
            }.onFailure {
                error.postValue(it.toString())
            }
        }
    }

    fun updateProfile(
        id: String,
        nickname: String?,
        birthday: String?,
        gender: Gender?,
    ) {
        viewModelScope.launchWithLoading {
            authRepository.updateProfile(
                id = id,
                login = nickname,
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