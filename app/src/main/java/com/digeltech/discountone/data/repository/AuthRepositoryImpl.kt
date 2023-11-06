package com.digeltech.discountone.data.repository

import com.digeltech.discountone.data.mapper.UserMapper
import com.digeltech.discountone.data.source.remote.api.AuthApi
import com.digeltech.discountone.domain.repository.AuthRepository
import com.digeltech.discountone.ui.common.KEY_USER
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
) : AuthRepository {

    override suspend fun register(nickname: String, email: String, password: String) = withContext(Dispatchers.IO) {
        runCatching {
            api.registerAccount(nickname, email, password).id
        }
    }

    override suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        runCatching {
            UserMapper().map(api.login(email = email, password = password)).also {
                Hawk.put(KEY_USER, it)
            }
        }
    }

    override suspend fun updateProfileWithAvatar(
        id: String,
        nickname: String?,
        birthday: String?,
        gender: String?,
        userAvatar: MultipartBody.Part?
    ) = withContext(Dispatchers.IO) {
        runCatching {
            UserMapper().map(
                api.updateProfileWithAvatar(
                    id = id,
                    birthday = birthday,
                    nickname = nickname,
                    gender = gender?.lowercase(),
                    file = userAvatar
                )
            ).also {
                Hawk.put(KEY_USER, it)
            }
        }
    }

    override suspend fun updateProfile(
        id: String,
        nickname: String?,
        birthday: String?,
        gender: String?,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            UserMapper().map(
                api.updateProfile(
                    id = id,
                    birthday = birthday,
                    gender = gender?.lowercase(),
                    nickname = nickname,
                )
            ).also {
                Hawk.put(KEY_USER, it)
            }
        }
    }

}