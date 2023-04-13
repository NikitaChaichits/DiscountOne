package com.digeltech.appdiscountone.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun copyTextToClipboard(context: Context?, text: String) {
    (context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.apply {
        setPrimaryClip(ClipData.newPlainText(text, text))
    }
}