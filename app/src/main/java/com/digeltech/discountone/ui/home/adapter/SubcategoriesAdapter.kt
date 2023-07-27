package com.digeltech.discountone.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.databinding.RvHomeSubcategoriesBinding
import com.digeltech.discountone.domain.model.CategoryWithDeals
import com.digeltech.discountone.ui.common.adapter.LinearDealAdapter
import com.digeltech.discountone.ui.common.model.CategoryWithDealsParcelable
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.digeltech.discountone.ui.common.model.toParcelableList
import com.digeltech.discountone.util.view.gone
import com.digeltech.discountone.util.view.visible

class SubcategoriesAdapter(
    private val onMoreDealsClick: (category: CategoryWithDealsParcelable) -> Unit,
    private val onDealClick: (deal: DealParcelable) -> Unit,
) : ListAdapter<CategoryWithDeals, SubcategoriesAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvHomeSubcategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvHomeSubcategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryWithDeals) {
            with(binding) {
                if (item.showParentName) {
                    tvCategoryTitle.text = item.parentName
                    tvCategoryTitle.visible()
                }

                tvSubcategoryTitle.text = item.name
                tvMoreDeals.setOnClickListener {
                    onMoreDealsClick(item.toParcelableList())
                }

                val dealsAdapter = LinearDealAdapter { onDealClick(it) }

                dealsAdapter.submitList(item.items.take(5).toParcelableList())
                rvDeals.adapter = dealsAdapter
            }
        }

        fun unbind() {
            binding.tvCategoryTitle.gone()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryWithDeals>() {
        override fun areItemsTheSame(oldItem: CategoryWithDeals, newItem: CategoryWithDeals): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CategoryWithDeals, newItem: CategoryWithDeals): Boolean =
            oldItem == newItem
    }
}