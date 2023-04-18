package com.digeltech.appdiscountone.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.appdiscountone.databinding.RvFrHomeCategoriesBinding
import com.digeltech.appdiscountone.domain.model.CategoryWithItems
import com.digeltech.appdiscountone.ui.common.adapter.DealAdapter
import com.digeltech.appdiscountone.ui.common.model.DealParcelable

class CategoriesAdapter(
    private val onMoreDealsClick: (categoryId: Int) -> Unit,
    private val onDealClick: (deal: DealParcelable) -> Unit,
) : ListAdapter<CategoryWithItems, CategoriesAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvFrHomeCategoriesBinding.inflate(
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

    inner class ItemViewholder(val binding: RvFrHomeCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryWithItems) {
            with(binding) {
                tvCategoryTitle.text = item.name
                tvMoreDeals.setOnClickListener { onMoreDealsClick(item.id) }

                val dealsAdapter = DealAdapter {
//                    onDealClick(item.items.)
                }
                dealsAdapter.submitList(item.items)
                rvDeals.adapter = dealsAdapter
            }
        }

        fun unbind() = Unit
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryWithItems>() {
        override fun areItemsTheSame(oldItem: CategoryWithItems, newItem: CategoryWithItems): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CategoryWithItems, newItem: CategoryWithItems): Boolean =
            oldItem == newItem
    }
}