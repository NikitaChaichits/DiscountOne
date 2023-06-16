package com.digeltech.appdiscountone.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.databinding.RvBannerBinding
import com.digeltech.appdiscountone.util.view.setImageWithRadius


class BannerAdapter(
    private val onClickListener: (Pair<Int?, Int?>) -> Unit,
) : ListAdapter<Banner, BannerAdapter.ItemViewholder>(DiffCallback()) {

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

        fun bind(item: Banner) {
            binding.ivBannerImage.apply {
                setImageWithRadius(item.urlImage, R.dimen.radius_16)
                setOnClickListener { onClickListener(Pair(item.dealId, item.categoryId)) }
            }
        }

        fun unbind() {
            binding.ivBannerImage.setImageDrawable(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Banner>() {
        override fun areItemsTheSame(oldItem: Banner, newItem: Banner): Boolean = oldItem.dealId == newItem.dealId

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Banner, newItem: Banner): Boolean = oldItem == newItem
    }
}