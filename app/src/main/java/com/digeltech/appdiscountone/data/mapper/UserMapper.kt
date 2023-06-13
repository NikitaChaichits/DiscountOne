package com.digeltech.appdiscountone.data.mapper

import com.digeltech.appdiscountone.data.model.UserDto
import com.digeltech.appdiscountone.domain.model.User

class UserMapper {

    fun map(data: UserDto): User = data.mapToUser()

    private fun UserDto.mapToUser() = User(
        id = userData.data.id.toString(),
        login = userData.data.login,
        email = userData.data.email,
        dateRegistration = userData.data.dateRegistration,
        city = userMetaData.city ?: "",
        birthdate = userMetaData.birthdate ?: ""
    )

}