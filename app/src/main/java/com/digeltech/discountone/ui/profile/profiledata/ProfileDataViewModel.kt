package com.digeltech.discountone.ui.profile.profiledata

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
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
        login: String?,
        city: String?,
        birthday: String?,
        userAvatar: MultipartBody.Part?
    ) {
        viewModelScope.launchWithLoading {
            authRepository.updateProfileWithAvatar(
                id = id,
                city = city,
                birthday = birthday,
                login = login,
                userAvatar = userAvatar
            ).onSuccess {
                success.postValue(true)
            }.onFailure {
                error.postValue(it.toString())
            }
        }
    }

    fun updateProfile(id: String, login: String?, city: String?, birthday: String?) {
        viewModelScope.launchWithLoading {
            authRepository.updateProfile(
                id = id,
                city = city,
                birthday = birthday,
                login = login,
            ).onSuccess {
                success.postValue(true)
            }.onFailure {
                error.postValue(it.toString())
            }
        }
    }
}