package com.digeltech.discountone.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.databinding.RvHomeCategoriesBinding
import com.digeltech.discountone.domain.model.CategoryWithSubcategories
import com.digeltech.discountone.ui.common.model.CategoryWithDealsParcelable
import com.digeltech.discountone.ui.common.model.DealParcelable
import com.facebook.appevents.AppEventsLogger

class CategoriesAdapter(
    private val onMoreDealsClick: (category: CategoryWithDealsParcelable) -> Unit,
    private val onDealClick: (deal: DealParcelable) -> Unit,
    private val logger: AppEventsLogger
) : ListAdapter<CategoryWithSubcategories, CategoriesAdapter.ItemViewholder>(DiffCallback()) {

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

        fun bind(item: CategoryWithSubcategories) {
            with(binding) {
                tvCategoryTitle.text = "${item.name}:"

                val subcategoriesAdapter = SubcategoriesAdapter(
                    onMoreDealsClick = {
                        onMoreDealsClick(it)
                    },
                    onDealClick = {
                        onDealClick(it)
                    },
                    logger = logger
                )

                subcategoriesAdapter.submitList(item.subcategories)
                rvSubcategories.adapter = subcategoriesAdapter
            }
        }

        fun unbind() = Unit
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryWithSubcategories>() {
        override fun areItemsTheSame(oldItem: CategoryWithSubcategories, newItem: CategoryWithSubcategories): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: CategoryWithSubcategories,
            newItem: CategoryWithSubcategories
        ): Boolean = oldItem == newItem
    }
}