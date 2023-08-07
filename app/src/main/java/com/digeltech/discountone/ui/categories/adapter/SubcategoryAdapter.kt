package com.digeltech.discountone.ui.categories.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.databinding.RvSubcategoryBinding
import com.digeltech.discountone.domain.model.Subcategory
import com.digeltech.discountone.util.view.loadImage

class SubcategoryAdapter(
    private val onSubcategoryClickListener: (Subcategory) -> Unit,
) : ListAdapter<Subcategory, SubcategoryAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvSubcategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvSubcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Subcategory) {
            with(binding) {
                root.setOnClickListener {
                    onSubcategoryClickListener(item)
                }
                tvTitle.text = item.name
                item.icon?.let(ivIcon::loadImage)
            }
        }

        fun unbind() {
            binding.ivIcon.setImageDrawable(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Subcategory>() {
        override fun areItemsTheSame(oldItem: Subcategory, newItem: Subcategory): Boolean = oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Subcategory, newItem: Subcategory): Boolean = oldItem == newItem
    }
}