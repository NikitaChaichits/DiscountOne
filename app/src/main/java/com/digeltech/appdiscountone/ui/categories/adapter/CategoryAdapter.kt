package com.digeltech.appdiscountone.ui.categories.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.appdiscountone.databinding.RvCategoryAndShopBinding
import com.digeltech.appdiscountone.domain.model.Category
import com.digeltech.appdiscountone.util.view.loadImage

class CategoryAdapter(
    private val onClickListener: (Pair<Int, String>) -> Unit,
) : ListAdapter<Category, CategoryAdapter.ItemViewholder>(DiffCallback()) {

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

        fun bind(item: Category) {
            with(binding) {
                tvTitle.text = item.name
                tvSubtitle.text = "${item.countOfItems} publications"

                item.icon?.let { ivIcon.loadImage(it) }

                root.setOnClickListener {
                    onClickListener(Pair(item.id, item.name))
                }
            }
        }

        fun unbind() {
            binding.ivIcon.setImageDrawable(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean = oldItem == newItem
    }
}