package com.digeltech.discountone.ui.common.model

import android.os.Parcelable
import com.digeltech.discountone.domain.model.CategoryWithDeals
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CategoryWithDealsParcelable(
    val id: Int,
    val name: String,
    val slug: String,
    val items: List<DealParcelable>
) : Parcelable

fun CategoryWithDeals.toParcelableList(): CategoryWithDealsParcelable {
    return CategoryWithDealsParcelable(
        id = id,
        name = name,
        slug = slug,
        items = items.toParcelableList()
    )

}