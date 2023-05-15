@file:Suppress("DEPRECATION")

package com.digeltech.appdiscountone.util.view

import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.digeltech.appdiscountone.R

fun Context.toast(message: String) {

    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    val view = toast.view
    val text = view?.findViewById(android.R.id.message) as TextView?
    val color = getColor(this, R.color.black)

    view?.background?.colorFilter =
        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN)
    text?.setTextColor(getColor(this, android.R.color.white))

    toast.show()
}


fun Context.toast(@StringRes message: Int) {

    val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
    val view = toast.view
    val text = view?.findViewById(android.R.id.message) as TextView?
    val color = getColor(this, R.color.black)

    view?.background?.colorFilter =
        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN)
    text?.setTextColor(getColor(this, android.R.color.white))

    toast.show()
}
