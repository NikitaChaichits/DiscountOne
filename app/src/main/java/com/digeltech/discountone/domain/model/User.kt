package com.digeltech.discountone.domain.model

data class User(
    val id: String,
    val login: String,
    val email: String,
    val dateRegistration: String,
    val city: String,
    val birthdate: String,
    val avatarUrl: String?,
    val gender: Gender?,
)

enum class Gender {
    MALE, FEMALE
}