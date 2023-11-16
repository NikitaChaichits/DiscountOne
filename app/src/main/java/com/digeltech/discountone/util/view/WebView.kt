package com.digeltech.discountone.util.view

import android.content.Context
import android.graphics.Bitmap
import android.provider.Settings.Secure
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient


fun WebView.openWebView(url: String, loader: View) {
    run {
//        webViewClient = WebViewClient()
        webViewClient = MyWebViewClient(loader, this)
        loadUrl(url)
        settings.javaScriptEnabled = true
        settings.setSupportZoom(true)
    }
}

class MyWebViewClient(private val loader: View, private val webView: WebView) : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        loader.gone()
        webView.visible()
    }
}

fun getAndroidID(context: Context): String? {
    return Secure.getString(context.contentResolver, Secure.ANDROID_ID)
}
