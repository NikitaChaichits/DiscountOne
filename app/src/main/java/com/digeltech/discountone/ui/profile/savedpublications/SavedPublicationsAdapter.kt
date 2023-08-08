package com.digeltech.discountone.ui.profile.savedpublications

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.R
import com.digeltech.discountone.databinding.RvDealGridBinding
import com.digeltech.discountone.ui.common.addToBookmark
import com.digeltech.discountone.ui.common.getCategoryNameById
import com.digeltech.discountone.ui.common.logShopNow
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.removeFromBookmark
import com.digeltech.discountone.util.capitalizeFirstLetter
import com.digeltech.discountone.util.copyTextToClipboard
import com.digeltech.discountone.util.getDiscountText
import com.digeltech.discountone.util.isNotNullAndNotEmpty
import com.digeltech.discountone.util.view.*
import com.facebook.appevents.AppEventsLogger
import javax.inject.Inject

class SavedPublicationsAdapter(
    private val onClickListener: (deal: DealParcelable) -> Unit,
) : ListAdapter<DealParcelable, SavedPublicationsAdapter.ItemViewholder>(DiffCallback()) {

    @Inject
    lateinit var logger: AppEventsLogger

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

                if (item.sale.isNotNullAndNotEmpty() && item.sale != "0") {
                    tvPriceWithDiscount.text = item.sale
                    tvPrice.gone()
                } else {
                    tvPrice.setStrikethrough(item.priceCurrency + item.oldPrice)
                    tvPriceWithDiscount.text =
                        getDiscountText(item.oldPrice?.toDouble() ?: 0.0, item.price?.toDouble() ?: 0.0)
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
                    it.openLink(item.shopLink)
                    logShopNow(
                        name = item.title,
                        url = item.shopLink,
                        shopName = item.shopName,
                        categoryName = getCategoryNameById(item.categoryId),
                        price = item.price.toString(),
                        className = "DealFragment",
                        context = it.context,
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
                    ivBookmark.setImageDrawable(ivBookmark.getImageDrawable(R.drawable.ic_bookmark_solid))
                }
                ivBookmark.setOnClickListener {
                    if (item.isAddedToBookmark) {
                        item.isAddedToBookmark = false
                        removeFromBookmark(item.id)

                        val list = currentList.toMutableList()
                        list.removeAt(adapterPosition)
                        submitList(list)

                        ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark))
                        it.context.toast(it.getString(R.string.removed_from_bookmarks))
                    } else {
                        item.isAddedToBookmark = true
                        addToBookmark(item)
                        ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark_solid))
                        it.context.toast(it.getString(R.string.added_to_bookmarks))
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