package com.digeltech.discountone.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.R
import com.digeltech.discountone.databinding.RvHomeShopBinding
import com.digeltech.discountone.domain.model.HomeShop
import com.digeltech.discountone.util.view.setImageWithRadius


class HomeShopsAdapter(
    private val onClickListener: (HomeShop) -> Unit,
) : ListAdapter<HomeShop, HomeShopsAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvHomeShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvHomeShopBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeShop) {
            binding.ivShopImage.apply {
                item.icon?.let {
                    setImageWithRadius(it, R.dimen.radius_16)
                    setOnClickListener { onClickListener(item) }
                }
            }
        }

        fun unbind() {
            binding.ivShopImage.setImageDrawable(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<HomeShop>() {
        override fun areItemsTheSame(oldItem: HomeShop, newItem: HomeShop): Boolean = oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: HomeShop, newItem: HomeShop): Boolean = oldItem == newItem
    }
}