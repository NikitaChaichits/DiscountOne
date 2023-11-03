package com.digeltech.discountone.data.source.local

import android.content.Context
import android.content.SharedPreferences
import com.digeltech.discountone.util.sharedpreferences.*
import javax.inject.Inject

class SharedPreferencesDataSource @Inject constructor(applicationContext: Context) {

    private val sharedPrefs: SharedPreferences = applicationContext
        .getSharedPreferences(KEY_SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun clear() = sharedPrefs.clear()

    /**
     * Хранение авторизован ли Пользователь
     */
    fun setLogin(isLogin: Boolean) = sharedPrefs.put(IS_LOGIN, isLogin)

    fun isLogin(): Boolean = sharedPrefs.get(IS_LOGIN, false)

    /**
     * Хранение имени Пользователя
     */
    fun setFirstLaunch(isFirstLaunch: Boolean) = sharedPrefs.put(FIRST_LAUNCH, isFirstLaunch)

    fun isFirstLaunch(): Boolean = sharedPrefs.get(FIRST_LAUNCH, true)

    /**
     * Хранение текущей темы Пользователя
     */
    fun setSignUpPromo(isShown: Boolean) = sharedPrefs.put(SIGN_UP_PROMO, isShown)

    fun getSignUpPromo(): Boolean = sharedPrefs.get(SIGN_UP_PROMO, false)

}
