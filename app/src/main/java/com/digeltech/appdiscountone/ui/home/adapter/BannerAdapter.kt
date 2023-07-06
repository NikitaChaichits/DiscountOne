package com.digeltech.appdiscountone.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.databinding.RvBannerBinding
import com.digeltech.appdiscountone.domain.model.Deal
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelable
import com.digeltech.appdiscountone.util.view.setImageWithRadius


class BannerAdapter(
    private val onClickListener: (DealParcelable) -> Unit,
) : ListAdapter<Deal, BannerAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Deal) {
            binding.ivBannerImage.apply {
                item.bannerImageUrl?.let {
                    setImageWithRadius(item.bannerImageUrl, R.dimen.radius_16)
                    setOnClickListener { onClickListener(item.toParcelable()) }
                }
            }
        }

        fun unbind() {
            binding.ivBannerImage.setImageDrawable(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Deal>() {
        override fun areItemsTheSame(oldItem: Deal, newItem: Deal): Boolean = oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Deal, newItem: Deal): Boolean = oldItem == newItem
    }
}