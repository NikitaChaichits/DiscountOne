package com.digeltech.discountone.util.view

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DimenRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.digeltech.discountone.R
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory as DCFF

val glideFactory: DCFF = DCFF.Builder().setCrossFadeEnabled(true).build()
val glideTransitionOption = DrawableTransitionOptions.withCrossFade(glideFactory)
val glideCircleTransformOption = RequestOptions().apply(RequestOptions.circleCropTransform())


fun ImageView.setCircleImage(
    uri: Uri?,
) {
    Glide.with(this)
        .load(uri)
        .apply(glideCircleTransformOption)
        .transition(glideTransitionOption)
        .into(this)
}

fun ImageView.setProfileImage(
    url: String,
) {
    Glide.with(this)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.drawable.ic_avatar)
        .apply(glideCircleTransformOption)
        .transition(glideTransitionOption)
        .into(this)
}

fun ImageView.setImageWithRadius(
    url: String,
    @DimenRes radiusDimen: Int = R.dimen.radius_12
) {
    val radius: Int = context.resources.getDimension(radiusDimen).toInt()
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .transform(FitCenter(), RoundedCorners(radius))
        .transition(glideTransitionOption)
        .into(this)
}

fun ImageView.loadImage(
    url: String,
) {
    Glide.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .transition(glideTransitionOption)
        .into(this)
}

fun ImageView.loadGif() {
    Glide
        .with(context)
        .load(R.raw.loading)
        .into(this)
}