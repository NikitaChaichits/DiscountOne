package com.digeltech.discountone.ui.common.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.R
import com.digeltech.discountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.discountone.databinding.RvDealLinearBinding
import com.digeltech.discountone.ui.common.WishlistDialogFragment
import com.digeltech.discountone.ui.common.addToBookmarkCache
import com.digeltech.discountone.ui.common.isAddedToBookmark
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.removeFromBookmarkCache
import com.digeltech.discountone.util.capitalizeFirstLetter
import com.digeltech.discountone.util.getDiscountText
import com.digeltech.discountone.util.view.*
import com.facebook.appevents.AppEventsLogger

class LinearDealAdapter(
    private val onClickListener: (deal: DealParcelable) -> Unit,
    private val onBookmarkClickListener: (dealId: Int) -> Unit,
    private val fragmentManager: FragmentManager,
    private val logger: AppEventsLogger,
) : ListAdapter<DealParcelable, LinearDealAdapter.ItemViewholder>(DiffCallback()) {

    private lateinit var prefs: SharedPreferencesDataSource

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvDealLinearBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvDealLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DealParcelable) {
            with(binding) {
                root.setOnClickListener { onClickListener(item) }
                btnGetDeal.setOnClickListener { onClickListener(item) }
                btnGetCoupon.setOnClickListener { onClickListener(item) }

                if (item.dealType == DealType.DISCOUNTS) discountDataSetting(item)
                else couponDataSetting(item)

                item.isAddedToBookmark = isAddedToBookmark(item.id)

                if (item.isAddedToBookmark) {
                    ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_wishlist_on))
                } else {
                    ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_wishlist_colored))
                }
                ivBookmark.setOnClickListener {
                    prefs = SharedPreferencesDataSource(it.context)

                    if (item.isAddedToBookmark) {
                        item.isAddedToBookmark = false
                        removeFromBookmarkCache(item.id)
                        onBookmarkClickListener(item.id)
                        ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_wishlist_colored))
                        it.context.toast(it.getString(R.string.removed_from_bookmarks))
                    } else {
                        if (!prefs.isLogin()) {
                            val dialogFragment = WishlistDialogFragment()
                            dialogFragment.show(fragmentManager, dialogFragment.tag)
                        } else {
                            item.isAddedToBookmark = true
                            addToBookmarkCache(item)
                            onBookmarkClickListener(item.id)
                            ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_wishlist_on))
                            it.context.toast(it.getString(R.string.added_to_bookmarks))
                        }
                    }
                }
            }
        }

        private fun RvDealLinearBinding.discountDataSetting(item: DealParcelable) {
            grDiscountUnique.visible()
            grCouponUnique.gone()

            item.imageUrl.let(ivDealImage::setImageWithRadius)
            tvTitle.text = item.title
            tvPrice.setStrikethrough(item.priceCurrency + item.oldPrice)
            tvPriceWithDiscount.text = getDiscountText(
                price = item.oldPrice,
                discountPrice = item.price,
                saleSize = item.saleSize,
            )

            item.shopImageUrl?.let { ivShopLogo.setImageWithRadius(it, R.dimen.radius_10) }
            if (item.shopName.isNotEmpty()) {
                tvShopName.text = item.shopName.capitalizeFirstLetter()
            }
        }

        private fun RvDealLinearBinding.couponDataSetting(item: DealParcelable) {
            grDiscountUnique.gone()
            grCouponUnique.visible()

            item.imageUrl.let(ivCouponImage::setImageWithRadius)
            tvCouponTitle.text = item.title
            if (item.price != 0) {
                tvCouponPrice.backgroundTintList =
                    ContextCompat.getColorStateList(tvPrice.context, R.color.couponPriceColor)
                tvCouponPrice.text = "Rs ${item.price} OFF"
            }
            if (item.saleSize != 0) {
                tvCouponPrice.text = "${item.saleSize}% OFF"
                tvCouponPrice.backgroundTintList =
                    ContextCompat.getColorStateList(tvPrice.context, R.color.couponDiscountColor)
            }

            if (item.price == 0 && item.saleSize == 0) {
                if (item.couponsTypeSlug == "free_trial") {
                    tvCouponPrice.backgroundTintList =
                        ContextCompat.getColorStateList(tvPrice.context, R.color.couponFreeTrialColor)
                } else {
                    tvCouponPrice.backgroundTintList =
                        ContextCompat.getColorStateList(tvPrice.context, R.color.couponFreeGiftColor)
                }
                tvCouponPrice.text = item.couponsTypeName
            }
            item.shopImageUrl?.let(ivCouponShopImage::setImageWithRadius)
            tvCouponCategoryName.text = item.couponsCategory
            tvPublishedDate.text = item.expirationDate
        }

        fun unbind() {
            binding.ivDealImage.setImageDrawable(null)
            binding.ivCouponImage.setImageDrawable(null)
            binding.ivCouponShopImage.setImageDrawable(null)
            binding.ivShopLogo.setImageDrawable(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DealParcelable>() {
        override fun areItemsTheSame(oldItem: DealParcelable, newItem: DealParcelable): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DealParcelable, newItem: DealParcelable): Boolean = oldItem == newItem
    }
}