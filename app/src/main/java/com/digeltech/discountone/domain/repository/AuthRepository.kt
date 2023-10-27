package com.digeltech.discountone.domain.repository

import com.digeltech.discountone.domain.model.User
import okhttp3.MultipartBody

interface AuthRepository {

    suspend fun register(login: String, email: String, password: String): Result<String>

    suspend fun login(email: String, password: String): Result<User>

    suspend fun updateProfileWithAvatar(
        id: String,
        login: String?,
//        city: String?,
        birthday: String?,
        gender: String?,
        userAvatar: MultipartBody.Part?
    ): Result<User>

    suspend fun updateProfile(
        id: String,
        login: String?,
//        city: String?,
        birthday: String?,
        gender: String?,
    ): Result<User>

}