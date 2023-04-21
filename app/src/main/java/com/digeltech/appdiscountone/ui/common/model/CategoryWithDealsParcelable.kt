package com.digeltech.appdiscountone.ui.common.model

import android.os.Parcelable
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CategoryWithDealsParcelable(
    val id: Int,
    val name: String,
    val items: List<DealParcelable>
) : Parcelable

fun CategoryWithDeals.toParcelableList(): CategoryWithDealsParcelable {
    return CategoryWithDealsParcelable(
        id = id,
        name = name,
        items = items.toParcelableList()
    )

}