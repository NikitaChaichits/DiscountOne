package com.digeltech.discountone.data.repository

import com.digeltech.discountone.data.mapper.UserMapper
import com.digeltech.discountone.data.source.remote.api.AuthApi
import com.digeltech.discountone.domain.repository.AuthRepository
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.util.log
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
) : AuthRepository {

    override suspend fun register(login: String, email: String, password: String) = withContext(Dispatchers.IO) {
        runCatching {
            api.registerAccount(login, email, password).id
        }
    }

    override suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.login(email = email, password = password)
            UserMapper().map(response).also {
                Hawk.put(KEY_USER, it)
                log("AuthRepositoryImpl $it")
            }
        }
    }

    override suspend fun updateProfileWithAvatar(
        id: String,
        login: String?,
        city: String?,
        birthday: String?,
        userAvatar: MultipartBody.Part?
    ) = withContext(Dispatchers.IO) {
        runCatching {
            api.updateProfileWithAvatar(
                id = id,
                city = city,
                birthday = birthday,
                nickname = login,
                file = userAvatar
            )
        }
    }

    override suspend fun updateProfile(
        id: String,
        login: String?,
        city: String?,
        birthday: String?,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            api.updateProfile(
                id = id,
                city = city,
                birthday = birthday,
                nickname = login,
            )
        }
    }

}