package com.digeltech.discountone.util.view

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.digeltech.discountone.R
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.ui.common.model.Taxonomy

fun categoriesStyledAdapter(context: Context, list: List<Item>) =
    object : ArrayAdapter<String>(context, R.layout.spinner_item, getNamesWithFirstAllString(list)) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent) as TextView

            if (position > 0) {
                val category = list[position - 1]
                if (category.isParent) {
                    if (category.taxonomy == Taxonomy.COUPONS.type)
                        view.setTextColor(ContextCompat.getColor(context, R.color.green))
                    else
                        view.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    view.setTypeface(null, Typeface.BOLD)
                    view.paintFlags = view.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                } else {
                    view.setTextColor(ContextCompat.getColor(context, R.color.defaultColor))
                    view.setTypeface(null, Typeface.NORMAL)
                    view.paintFlags = view.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                }
            }

            return view
        }
    }

fun getNamesWithFirstAllString(data: List<Item>): List<String> {
    val names: List<String> = data.map(Item::name)
    val mutableList = mutableListOf("All")
    mutableList.addAll(names)
    return mutableList
}