package com.digeltech.discountone.util

import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import com.digeltech.discountone.util.view.gone
import com.digeltech.discountone.util.view.visible
import com.google.android.material.transition.platform.MaterialFade
import com.google.android.material.transition.platform.MaterialFadeThrough

fun createFadeTransition(
    screenRoot: ViewGroup,
    targetView: View,
    shouldHide: Boolean = false
) {
    val materialFade = MaterialFade().apply {
        duration = 300L
    }
    TransitionManager.beginDelayedTransition(screenRoot, materialFade)

    targetView.apply {
        if (shouldHide) gone() else visible()
    }
}

fun createFadeThroughTransition(
    screenRoot: ViewGroup,
    outgoingView: View,
    incomingView: View,
) {
    val fadeThrough = MaterialFadeThrough()
    TransitionManager.beginDelayedTransition(screenRoot, fadeThrough)

    outgoingView.gone()
    incomingView.visible()
}