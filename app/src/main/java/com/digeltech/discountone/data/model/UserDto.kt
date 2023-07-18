package com.digeltech.discountone.data.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("user_data")
    val userData: UserDataDto,
    @SerializedName("user_metadata")
    val userMetaData: UserMetaDataDto
)

data class UserDataDto(
    @SerializedName("data")
    val data: DataDto
)

data class UserMetaDataDto(
    @SerializedName("city")
    val city: String?,
    @SerializedName("my_birthday")
    val birthdate: String?,
)

data class DataDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_login")
    val login: String,
    @SerializedName("user_email")
    val email: String,
    @SerializedName("user_registered")
    val dateRegistration: String,

    )
