package com.digeltech.appdiscountone.domain.model

data class User(
    val id: String,
    val login: String,
    val email: String,
    val dateRegistration: String,
    val city: String,
    val birthdate: String,
)
