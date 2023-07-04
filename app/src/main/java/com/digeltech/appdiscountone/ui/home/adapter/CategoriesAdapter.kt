package com.digeltech.appdiscountone.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.databinding.RvHomeCategoriesBinding
import com.digeltech.appdiscountone.domain.model.CategoryWithDeals
import com.digeltech.appdiscountone.ui.common.adapter.LinearDealAdapter
import com.digeltech.appdiscountone.ui.common.model.CategoryWithDealsParcelable
import com.digeltech.appdiscountone.ui.common.model.DealParcelable
import com.digeltech.appdiscountone.ui.common.model.toParcelableList

class CategoriesAdapter(
    private val onBestDealsClick: () -> Unit,
    private val onMoreDealsClick: (category: CategoryWithDealsParcelable) -> Unit,
    private val onDealClick: (deal: DealParcelable) -> Unit,
) : ListAdapter<CategoryWithDeals, CategoriesAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvHomeCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvHomeCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryWithDeals) {
            with(binding) {
                tvCategoryTitle.text = item.name
                tvMoreDeals.setOnClickListener {
                    if (item.name.equals(it.context.getString(R.string.nav_deals), true))
                        onBestDealsClick()
                    else
                        onMoreDealsClick(item.toParcelableList())
                }

                val dealsAdapter = LinearDealAdapter { onDealClick(it) }

                dealsAdapter.submitList(item.items.toParcelableList())
                rvDeals.adapter = dealsAdapter
            }
        }

        fun unbind() = Unit
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryWithDeals>() {
        override fun areItemsTheSame(oldItem: CategoryWithDeals, newItem: CategoryWithDeals): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CategoryWithDeals, newItem: CategoryWithDeals): Boolean =
            oldItem == newItem
    }
}