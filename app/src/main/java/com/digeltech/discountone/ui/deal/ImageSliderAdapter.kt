package com.digeltech.discountone.ui.deal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.digeltech.discountone.R
import com.digeltech.discountone.util.view.glideTransitionOption

class ImageSliderAdapter(private val context: Context, private val imageUrls: List<String>) :
    PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_images, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.ivSliderImage)
        Glide.with(context)
            .load(imageUrls[position])
            .transition(glideTransitionOption)
            .into(imageView)

        container.addView(itemView)
        return itemView
    }

    override fun getCount(): Int = imageUrls.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) = container.removeView(obj as View)
}
