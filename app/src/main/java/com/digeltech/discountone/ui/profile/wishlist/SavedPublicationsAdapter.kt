package com.digeltech.discountone.ui.profile.wishlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.R
import com.digeltech.discountone.databinding.RvDealGridBinding
import com.digeltech.discountone.ui.common.addToBookmarkCache
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.common.removeFromBookmarkCache
import com.digeltech.discountone.util.capitalizeFirstLetter
import com.digeltech.discountone.util.getDiscountText
import com.digeltech.discountone.util.isNotNullAndNotEmpty
import com.digeltech.discountone.util.time.formatDate
import com.digeltech.discountone.util.view.*
import com.facebook.appevents.AppEventsLogger

/**
 * Нельзя использовать GridDealAdapter, т.к. надо удалять закладку с экрана (111 строка)
 */
class SavedPublicationsAdapter(
    private val onClickListener: (deal: DealParcelable) -> Unit,
    private val emptyListCallback: () -> Unit,
    private val onBookmarkClickListener: (dealId: Int) -> Unit,
    private val logger: AppEventsLogger
) : ListAdapter<DealParcelable, SavedPublicationsAdapter.ItemViewholder>(DiffCallback()) {

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
                root.setOnClickListener { onClickListener(item) }
                btnGetDeal.setOnClickListener { onClickListener(item) }
                btnGetCoupon.setOnClickListener { onClickListener(item) }

                if (item.dealType == DealType.DISCOUNTS) discountDataSetting(item)
                else couponDataSetting(item)

                item.imageUrl.let(ivDealImage::setImageWithRadius)
                tvTitle.text = item.title

                item.shopImageUrl?.let { ivShopLogo.setImageWithRadius(it, R.dimen.radius_10) }
                if (item.shopName.isNotNullAndNotEmpty()) {
                    tvShopName.text = item.shopName?.capitalizeFirstLetter()
                }

                if (item.isAddedToBookmark) {
                    ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_wishlist_on))
                    tvWishlist.text = itemView.getString(R.string.fr_deal_remove_from_bookmarks)
                } else {
                    ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_wishlist_colored))
                    tvWishlist.text = itemView.getString(R.string.fr_deal_add_to_bookmarks)
                }
                ivBookmark.setOnClickListener {
                    updateWishlist(item, this@ItemViewholder, this, it)
                }
                tvWishlist.setOnClickListener {
                    updateWishlist(item, this@ItemViewholder, this, it)
                }
            }
        }

        private fun RvDealGridBinding.discountDataSetting(item: DealParcelable) {
            grDiscountUnique.visible()
            grCouponUnique.gone()

            tvPublishedDate.text = "Updated: ${formatDate(item.lastUpdateDate.toString())}"
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

        private fun RvDealGridBinding.couponDataSetting(item: DealParcelable) {
            grDiscountUnique.invisible()
            grCouponUnique.visible()

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
            binding.ivCouponShopImage.setImageDrawable(null)
            binding.ivShopLogo.setImageDrawable(null)
        }

        private fun updateWishlist(
            item: DealParcelable,
            itemViewholder: ItemViewholder,
            rvDealWishlistBinding: RvDealGridBinding,
            it: View
        ) {
            if (item.isAddedToBookmark) {
                item.isAddedToBookmark = false
                removeFromBookmarkCache(item.id)
                onBookmarkClickListener(item.id)

                val list = currentList.toMutableList()
                list.removeAt(itemViewholder.adapterPosition)
                submitList(list)

                if (list.isEmpty()) emptyListCallback()

                rvDealWishlistBinding.ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_wishlist_colored))
                it.context.toast(it.getString(R.string.removed_from_bookmarks))
            } else {
                item.isAddedToBookmark = true
                addToBookmarkCache(item)
                onBookmarkClickListener(item.id)
                rvDealWishlistBinding.ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_wishlist_on))
                it.context.toast(it.getString(R.string.added_to_bookmarks))
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DealParcelable>() {
        override fun areItemsTheSame(oldItem: DealParcelable, newItem: DealParcelable): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: DealParcelable, newItem: DealParcelable): Boolean = oldItem == newItem
    }
}