package com.digeltech.discountone.ui.shops.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.databinding.RvCategoryAndShopBinding
import com.digeltech.discountone.domain.model.Shop
import com.digeltech.discountone.util.view.loadImage

class ShopAdapter(
    private val onClickListener: (Shop) -> Unit,
) : ListAdapter<Shop, ShopAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvCategoryAndShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvCategoryAndShopBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Shop) {
            with(binding) {
                tvTitle.text = item.name
                tvSubtitle.text = "${item.countOfItems} deals"

                item.icon?.let { ivIcon.loadImage(it) }

                root.setOnClickListener {
                    onClickListener(item)
                }
            }
        }

        fun unbind() {
            binding.ivIcon.setImageDrawable(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Shop>() {
        override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean = oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean = oldItem == newItem
    }
}