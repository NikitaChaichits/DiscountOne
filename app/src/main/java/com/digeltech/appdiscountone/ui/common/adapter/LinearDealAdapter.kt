package com.digeltech.appdiscountone.ui.common.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.databinding.RvDealLinearBinding
import com.digeltech.appdiscountone.ui.common.addToBookmark
import com.digeltech.appdiscountone.ui.common.isAddedToBookmark
import com.digeltech.appdiscountone.ui.common.logShopNow
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.removeFromBookmark
import com.digeltech.appdiscountone.util.capitalizeFirstLetter
import com.digeltech.appdiscountone.util.copyTextToClipboard
import com.digeltech.appdiscountone.util.isNotNullAndNotEmpty
import com.digeltech.appdiscountone.util.view.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LinearDealAdapter(
    private val onClickListener: (deal: DealParcelable) -> Unit,
) : ListAdapter<DealParcelable, LinearDealAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvDealLinearBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::ItemViewholder)
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
                item.imageUrl?.let(ivDealImage::setImageWithRadius)

                if (item.sale.isNotNullAndNotEmpty()) {
                    tvPriceWithDiscount.text = item.sale
                    tvPrice.gone()
                } else {
                    tvPrice.setStrikethrough(item.priceCurrency + item.oldPrice)
                    tvPriceWithDiscount.text = item.priceCurrency + item.price
                }

                tvTitle.text = item.title

                item.shopImageUrl?.let { ivCouponCompanyLogo.setImageWithRadius(it, R.dimen.radius_10) }

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
                    it.openLink(item.link)
                    logShopNow(name = item.title, url = item.link)
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
                }
                ivBookmark.setOnClickListener {
                    if (item.isAddedToBookmark) {
                        item.isAddedToBookmark = false
                        removeFromBookmark(item.id)
                        ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark))
                        it.context.toast(it.getString(R.string.removed_from_bookmarks))
                    } else {
                        if (Firebase.auth.currentUser == null) {
                            it.context.toast(R.string.toast_bookmark)
                        } else {
                            item.isAddedToBookmark = true
                            addToBookmark(item)
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