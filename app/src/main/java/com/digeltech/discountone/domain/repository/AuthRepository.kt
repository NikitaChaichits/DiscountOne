package com.digeltech.discountone.domain.repository

interface AuthRepository {

    suspend fun register(login: String, email: String, password: String): Result<Unit>

    suspend fun login(email: String, password: String): Result<Unit>

    suspend fun updateProfile(id: String, login: String?, city: String?, birthday: String?): Result<Unit>

}