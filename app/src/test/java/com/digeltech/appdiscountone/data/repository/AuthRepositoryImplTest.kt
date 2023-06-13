package com.digeltech.appdiscountone.data.repository

import com.digeltech.appdiscountone.data.mapper.UserMapper
import com.digeltech.appdiscountone.data.model.UserDto
import com.digeltech.appdiscountone.data.source.remote.api.AuthApi
import com.google.gson.Gson
import org.junit.Before
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

internal class AuthRepositoryImplTest {

    @Mock
    private lateinit var api: AuthApi

    // Инициализируем mock зависимости перед каждым тестом
    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testJsonMapping() = runCatching {
        val userMapper = UserMapper()
        // Given
        val json = """
            {
                "user_data": {
                    "data": {
                        "user_login": "Вася",
                        "user_email": "nikitachaichits@gmail.com",
                        "user_registered": "2023-06-12 09:19:09",
                        "display_name": "nikitachaichits",
                        "id": 44
                    }
                },
                "user_metadata": {
                    "city": "Дзержинск",
                    "my_birthday": "1999-11-11"
                }
            }
        """

        val userDto = Gson().fromJson(json, UserDto::class.java)
        val user = userMapper.map(userDto)

        assertEquals("Вася", user.login)
        assertEquals("nikitachaichits@gmail.com", user.email)
        assertEquals("2023-06-12 09:19:09", user.dateRegistration)
        assertEquals("Дзержинск", user.city)
        assertEquals("1999-11-11", user.birthdate)
    }
}