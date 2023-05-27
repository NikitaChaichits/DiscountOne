package com.digeltech.appdiscountone.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.digeltech.appdiscountone.util.sharedpreferences.*
import javax.inject.Inject

class SharedPreferencesDataSource @Inject constructor(applicationContext: Context) {

    private val sharedPrefs: SharedPreferences = applicationContext
        .getSharedPreferences(KEY_SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun clear() = sharedPrefs.clear()

    /**
     * Хранение даты регистрации Пользователя
     */
    fun setDateOfRegistration(date: String) = sharedPrefs.put(DATE_OF_REGISTRATION, date)

    fun getDateOfRegistration(): String = sharedPrefs.get(DATE_OF_REGISTRATION, "")

    /**
     * Хранение имени Пользователя
     */
    fun setFirstLaunch(isFirstLaunch: Boolean) = sharedPrefs.put(FIRST_LAUNCH, isFirstLaunch)

    fun isFirstLaunch(): Boolean = sharedPrefs.get(FIRST_LAUNCH, true)

    /**
     * Хранение даты рождения Пользователя
     */
    fun setDateOfBirth(date: String) = sharedPrefs.put(DATE_OF_BIRTH, date)

    fun getDateOfBirth(): String = sharedPrefs.get(DATE_OF_BIRTH, "")

    /**
     * Хранение города Пользователя
     */
    fun setCity(name: String) = sharedPrefs.put(CITY, name)

    fun getCity(): String = sharedPrefs.get(CITY, "")

    /**
     * Хранение текущей темы Пользователя
     */
    fun setTheme(isDarkTheme: Boolean) = sharedPrefs.put(THEME, isDarkTheme)

    fun getTheme(): Boolean = sharedPrefs.get(THEME, false)
}
