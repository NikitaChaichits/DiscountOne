package com.digeltech.discountone.ui.profile.wishlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.R
import com.digeltech.discountone.databinding.RvDealWishlistBinding
import com.digeltech.discountone.ui.common.addToBookmarkCache
import com.digeltech.discountone.ui.common.getCategoryNameById
import com.digeltech.discountone.ui.common.logShopNow
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.removeFromBookmarkCache
import com.digeltech.discountone.util.capitalizeFirstLetter
import com.digeltech.discountone.util.copyTextToClipboard
import com.digeltech.discountone.util.getDiscountText
import com.digeltech.discountone.util.isNotNullAndNotEmpty
import com.digeltech.discountone.util.view.*
import com.facebook.appevents.AppEventsLogger

/**
 * Нельзя использовать GridDealAdapter, т.к. надо удалять закладку с экрана (111 строка)
 */
class SavedPublicationsAdapter(
    private val onClickListener: (deal: DealParcelable) -> Unit,
    private val emptyListCallback: () -> Unit,
    private val logger: AppEventsLogger
) : ListAdapter<DealParcelable, SavedPublicationsAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvDealWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvDealWishlistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DealParcelable) {
            with(binding) {
                item.imageUrl.let(ivDealImage::setImageWithRadius)

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
                if (item.shopName.isNotNullAndNotEmpty()) {
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

                if (item.isAddedToBookmark) {
                    ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_wishlist_on))
                    tvWishlist.text = itemView.getString(R.string.fr_deal_remove_from_bookmarks)
                } else {
                    ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_wishlist))
                    tvWishlist.text = itemView.getString(R.string.fr_deal_add_to_bookmarks)
                }
                ivBookmark.setOnClickListener {
                    addToWishlist(item, this@ItemViewholder, this, it)
                }
                tvWishlist.setOnClickListener {
                    addToWishlist(item, this@ItemViewholder, this, it)
                }
            }
        }

        fun unbind() {
            binding.ivDealImage.setImageDrawable(null)
            binding.ivCouponCompanyLogo.setImageDrawable(null)
            binding.ivRateArrow.setImageDrawable(null)
        }

        private fun addToWishlist(
            item: DealParcelable,
            itemViewholder: ItemViewholder,
            rvDealWishlistBinding: RvDealWishlistBinding,
            it: View
        ) {
            if (item.isAddedToBookmark) {
                item.isAddedToBookmark = false
                removeFromBookmarkCache(item.id)

                val list = currentList.toMutableList()
                list.removeAt(itemViewholder.adapterPosition)
                submitList(list)

                if (list.isEmpty()) emptyListCallback()

                rvDealWishlistBinding.ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_wishlist))
                it.context.toast(it.getString(R.string.removed_from_bookmarks))
            } else {
                item.isAddedToBookmark = true
                addToBookmarkCache(item)
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