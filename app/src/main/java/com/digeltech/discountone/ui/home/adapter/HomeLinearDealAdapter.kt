package com.digeltech.discountone.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.digeltech.discountone.ui.common.removeFromBookmarkCache
import com.digeltech.discountone.util.capitalizeFirstLetter
import com.digeltech.discountone.util.copyTextToClipboard
import com.digeltech.discountone.util.getDiscountText
import com.digeltech.discountone.util.isNotNullAndNotEmpty
import com.digeltech.discountone.util.view.*
import com.facebook.appevents.AppEventsLogger

class HomeLinearDealAdapter(
    private val onClickListener: (deal: DealParcelable) -> Unit,
    private val onBookmarkClickListener: (dealId: Int) -> Unit,
    private val fragmentManager: FragmentManager,
    private val logger: AppEventsLogger,
) : ListAdapter<DealParcelable, HomeLinearDealAdapter.ItemViewholder>(DiffCallback()) {

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

                btnGetDeal.setOnClickListener { onClickListener(item) }

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