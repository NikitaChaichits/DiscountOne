package com.digeltech.appdiscountone.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.digeltech.appdiscountone.util.sharedpreferences.*
import javax.inject.Inject

class SharedPreferencesDataSource @Inject constructor(applicationContext: Context) {

    private val sharedPrefs: SharedPreferences = applicationContext
        .getSharedPreferences(KEY_SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun clear() = sharedPrefs.clear()

    fun setDateOfRegistration(date: String) = sharedPrefs.put(DATE_OF_REGISTRATION, date)

    fun getDateOfRegistration(): String = sharedPrefs.get(DATE_OF_REGISTRATION, "")

    fun setName(name: String) = sharedPrefs.put(NAME, name)

    fun getName(): String = sharedPrefs.get(NAME, "")

    fun setDateOfBirth(date: String) = sharedPrefs.put(DATE_OF_BIRTH, date)

    fun getDateOfBirth(): String = sharedPrefs.get(DATE_OF_BIRTH, "")

    fun setCity(name: String) = sharedPrefs.put(CITY, name)

    fun getCity(): String = sharedPrefs.get(CITY, "")
}
