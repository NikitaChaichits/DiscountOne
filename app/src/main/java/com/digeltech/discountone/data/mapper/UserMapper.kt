package com.digeltech.discountone.data.mapper

import com.digeltech.discountone.data.model.UserDto
import com.digeltech.discountone.domain.model.Gender
import com.digeltech.discountone.domain.model.User

class UserMapper {

    fun map(data: UserDto): User = data.mapToUser()

    private fun UserDto.mapToUser() = User(
        id = userData.data.id.toString(),
        login = userData.data.login,
        email = userData.data.email,
        dateRegistration = userData.data.dateRegistration,
        city = userMetaData.city ?: "",
        birthdate = userMetaData.birthdate ?: "",
        avatarUrl = userData.data.avatarUrl,
        gender = when (userMetaData.gender) {
            "male" -> Gender.MALE
            "female" -> Gender.FEMALE
            else -> null
        }
    )

}