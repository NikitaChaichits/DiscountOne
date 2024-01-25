package com.digeltech.discountone.ui.common

import android.widget.ImageView
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.util.view.setProfileImage
import com.orhanobut.hawk.Hawk

fun loadProfileImage(iv: ImageView) {
    Hawk.get<User>(KEY_USER)?.let {
        it.avatarUrl?.let { url ->
            iv.setProfileImage(url)
        }
    }
}