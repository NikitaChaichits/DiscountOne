package com.digeltech.appdiscountone.ui.coupons.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.databinding.RvDealBinding
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.util.copyTextToClipboard
import com.digeltech.appdiscountone.util.getDiscountText
import com.digeltech.appdiscountone.util.view.*


class DealAdapter(
    private val onClickListener: (id: Int) -> Unit,
) : ListAdapter<Deal, DealAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvDealBinding.inflate(
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

    inner class ItemViewholder(val binding: RvDealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Deal) {
            with(binding) {
                item.imageUrl?.let(ivDealImage::loadImage)
                tvPrice.setStrikethrough(item.oldPrice.toString())
                tvPriceWithDiscount.text = item.discountPrice.toString()
                btnGetCoupon.text = getDiscountText(item.oldPrice, item.discountPrice)
                tvTitle.text = item.title

//                ivCouponCompanyLogo.setImageDrawable(ivCouponCompanyLogo.getImageDrawable(item.companyLogo))
                tvCouponCompany.text = item.companyName

                if (item.rating >= 0) {
                    ivRateArrow.setImageDrawable(ivRateArrow.getImageDrawable(R.drawable.ic_arrow_up))
                } else {
                    ivRateArrow.setImageDrawable(ivRateArrow.getImageDrawable(R.drawable.ic_arrow_down))
                }
                tvRate.text = item.rating.toString()

                btnGetCoupon.setOnClickListener {
                    onClickListener(item.id)
                }

                btnCopy.setOnClickListener {
                    copyTextToClipboard(it.context, item.title)
                    it.context.toast(it.getString(R.string.copied))
                }

                ivBookmark.setOnClickListener {
                    if (item.isAddedToBookmark) {
                        item.isAddedToBookmark = false
                        ivBookmark.setImageDrawable(it.getImageDrawable(R.drawable.ic_bookmark))
                        it.context.toast(it.getString(R.string.removed_from_bookmarks))
                    } else {
                        item.isAddedToBookmark = true
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

    class DiffCallback : DiffUtil.ItemCallback<Deal>() {
        override fun areItemsTheSame(oldItem: Deal, newItem: Deal): Boolean = oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Deal, newItem: Deal): Boolean = oldItem == newItem
    }
}