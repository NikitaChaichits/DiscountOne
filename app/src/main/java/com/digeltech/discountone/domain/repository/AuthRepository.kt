package com.digeltech.discountone.domain.repository

import com.digeltech.discountone.domain.model.User
import okhttp3.MultipartBody

interface AuthRepository {

    suspend fun register(nickname: String, email: String, password: String): Result<String>

    suspend fun login(email: String, password: String): Result<User>

    suspend fun updateProfileWithAvatar(
        id: String,
        nickname: String?,
//        city: String?,
        birthday: String?,
        gender: String?,
        userAvatar: MultipartBody.Part?
    ): Result<User>

    suspend fun updateProfile(
        id: String,
        nickname: String?,
//        city: String?,
        birthday: String?,
        gender: String?,
    ): Result<User>

    suspend fun resetPasswordFirstStep(email: String): Result<Unit>

    suspend fun resetPasswordSecondStep(userId: String, password: String): Result<Unit>

}