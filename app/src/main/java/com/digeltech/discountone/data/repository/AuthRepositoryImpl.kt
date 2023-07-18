package com.digeltech.discountone.data.repository

import com.digeltech.discountone.data.source.remote.api.AuthApi
import com.digeltech.discountone.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
) : AuthRepository {

    override suspend fun register(login: String, email: String, password: String) = withContext(Dispatchers.IO) {
        runCatching {
            api.registerAccount(login, email, password)
        }
    }

    override suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.login(email = email, password = password)
            val user = response.userData
//            UserMapper().map()).also {
//                Hawk.put(KEY_USER, it)
//            }
        }
    }

    override suspend fun updateProfile(id: String, login: String?, city: String?, birthday: String?) =
        withContext(Dispatchers.IO) {
            runCatching {
                api.updateProfile(
                    id = id,
                    city = city,
                    birthday = birthday,
                    nickname = login
                )
            }
        }

}