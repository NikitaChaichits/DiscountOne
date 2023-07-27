package com.digeltech.discountone.util.view

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DimenRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.digeltech.discountone.R
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory as DCFF

val glideFactory: DCFF = DCFF.Builder().setCrossFadeEnabled(true).build()
val glideTransitionOption = DrawableTransitionOptions.withCrossFade(glideFactory)
val glideCircleTransformOption = RequestOptions().apply(RequestOptions.circleCropTransform())


fun ImageView.setCircleImage(
    uri: Uri?,
//    @DrawableRes placeholderRes: Int,
    requestListener: RequestListener<Drawable>? = null
) {
    Glide.with(this)
        .load(uri)
        .apply(glideCircleTransformOption)
        .apply { if (requestListener != null) addListener(requestListener) }
//        .placeholder(placeholderRes)
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
        .transform(FitCenter(), RoundedCorners(radius))
        .transition(glideTransitionOption)
        .into(this)
}

fun ImageView.loadImage(
    url: String,
) {
    Glide.with(context)
        .load(url)
        .transition(glideTransitionOption)
        .into(this)
}

fun ImageView.loadGif() {
    Glide
        .with(context)
        .load(R.raw.loading)
        .into(this)
}

open class RequestListenerAdapter(
    private val onSuccess: () -> Unit = {},
    private val onFail: () -> Unit = {}
) : RequestListener<Drawable> {

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        onFail()
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        onSuccess()
        return false
    }
}
