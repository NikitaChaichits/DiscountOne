package com.digeltech.discountone.data.source.remote.api

import com.digeltech.discountone.data.model.UserDto
import com.digeltech.discountone.data.model.UserIdDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AuthApi {

    @POST("/wp-json/theme/v1/users/register")
    suspend fun registerAccount(
        @Query("login") login: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): UserIdDto

    @POST("/wp-json/theme/v1/users/auth")
    suspend fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): UserDto

    @Multipart
    @POST("/wp-json/theme/v1/user_upload")
    suspend fun updateProfileWithAvatar(
        @Query("id") id: String,
        @Query("city") city: String?,
        @Query("my_birthday") birthday: String?,
        @Query("nickname") nickname: String?,
        @Part file: MultipartBody.Part?
    ): UserDto

    @POST("/wp-json/theme/v1/user_upload")
    suspend fun updateProfile(
        @Query("id") id: String,
        @Query("city") city: String?,
        @Query("my_birthday") birthday: String?,
        @Query("nickname") nickname: String?,
    ): UserDto
}