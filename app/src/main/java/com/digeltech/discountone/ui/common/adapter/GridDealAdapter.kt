package com.digeltech.discountone.ui.common.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.R
import com.digeltech.discountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.discountone.databinding.RvDealGridBinding
import com.digeltech.discountone.ui.common.*
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.util.capitalizeFirstLetter
import com.digeltech.discountone.util.copyTextToClipboard
import com.digeltech.discountone.util.getDiscountText
import com.digeltech.discountone.util.isNotNullAndNotEmpty
import com.digeltech.discountone.util.time.formatDate
import com.digeltech.discountone.util.view.*
import com.facebook.appevents.AppEventsLogger

class GridDealAdapter(
    private val onClickListener: (deal: DealParcelable) -> Unit,
//    private val onBookmarkClickListener: (dealId: Int) -> Unit,
    private val logger: AppEventsLogger,
) : ListAdapter<DealParcelable, GridDealAdapter.ItemViewholder>(DiffCallback()) {

    private lateinit var prefs: SharedPreferencesDataSource

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvDealGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvDealGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DealParcelable) {
            with(binding) {
                item.imageUrl.let(ivDealImage::setImageWithRadius)

                tvPublishedDate.text = "Updated: ${formatDate(item.lastUpdateDate)}"
                if (item.sale.isNotNullAndNotEmpty() && item.sale != "0") {
                    tvPriceWithDiscount.text = item.sale
                    tvPrice.gone()
                } else {
                    tvPrice.setStrikethrough(item.priceCurrency + item.oldPrice)
                    tvPriceWithDiscount.text = getDiscountText(
                        price = item.oldPrice?.toDouble() ?: 0.0,
                        discountPrice = item.price?.toDouble() ?: 0.0,
                        saleSize = item.saleSize,
                    )
                }

                tvTitle.text = item.title

                item.shopImageUrl.let { ivCouponCompanyLogo.setImageWithRadius(it, R.dimen.radius_10) }

                if (item.shopName.isNotEmpty()) {
                    tvCouponCompany.text = item.shopName.capitalizeFirstLetter()
                }

                if (item.rating >= 0) {
                    ivRateArrow.setImageDrawable(ivRateArrow.getImageDrawable(R.drawable.ic_arrow_up))
                } else {
                    ivRateArrow.setImageDrawable(ivRateArrow.getImageDrawable(R.drawable.ic_arrow_down))
                }
                tvRate.text = item.rating.toString()

                root.setOnClickListener { onClickListener(item) }

                btnGetDeal.setOnClickListener {
                    onClickListener(item)
                    logShopNow(
                        name = item.title,
                        url = item.shopLink,
                        shopName = item.shopName,
                        categoryName = getCategoryNameById(item.categoryId),
                        price = item.price.toString(),
                        className = "DealFragment",
                        logger = logger
                    )
                }

                if (item.promocode.isNotNullAndNotEmpty()) {
                    btnCopy.visible()
                    btnCopy.setOnClickListener {
                        copyTextToClipboard(it.context, item.promocode!!)
                        it.context.toast(it.getString(R.string.copied))
                    }
                } else {
                    btnCopy.gone()
                }

                item.isAddedToBookmark = isAddedToBookmark(item.id)

                if (item.isAddedToBookmark) {
                    ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_bookmark_solid))
                } else {
                    ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_bookmark))
                }
                ivBookmark.setOnClickListener {
                    prefs = SharedPreferencesDataSource(it.context)

                    if (item.isAddedToBookmark) {
                        item.isAddedToBookmark = false
                        removeFromBookmarkCache(item.id)
                        ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark))
                        it.context.toast(it.getString(R.string.removed_from_bookmarks))
                    } else {
                        if (!prefs.isLogin()) {
                            it.context.toast(R.string.toast_bookmark)
                        } else {
                            item.isAddedToBookmark = true
                            addToBookmarkCache(item)
                            ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark_solid))
                            it.context.toast(it.getString(R.string.added_to_bookmarks))
                        }
                    }
                }
            }
        }

        fun unbind() {
            binding.ivDealImage.setImageDrawable(null)
            binding.ivCouponCompanyLogo.setImageDrawable(null)
            binding.ivRateArrow.setImageDrawable(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DealParcelable>() {
        override fun areItemsTheSame(oldItem: DealParcelable, newItem: DealParcelable): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DealParcelable, newItem: DealParcelable): Boolean = oldItem == newItem
    }
}