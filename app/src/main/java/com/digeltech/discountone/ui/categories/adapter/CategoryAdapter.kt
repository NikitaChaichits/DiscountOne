package com.digeltech.discountone.ui.categories.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.databinding.RvCategoryOldBinding
import com.digeltech.discountone.domain.model.Category
import com.digeltech.discountone.util.createFadeTransition
import com.digeltech.discountone.util.view.gone
import com.digeltech.discountone.util.view.invisible
import com.digeltech.discountone.util.view.loadImage
import com.digeltech.discountone.util.view.visible

class CategoryAdapter(
    private val onSubcategoryClickListener: (Pair<Int, String>) -> Unit,
) : ListAdapter<Pair<Category, Category?>, CategoryAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvCategoryOldBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvCategoryOldBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pair<Category, Category?>) {
            with(binding) {
                tvTitleOdd.text = item.first.name
                tvSubtitleOdd.text = "${item.first.countOfItems} deals"
                item.first.icon?.let(ivIconOdd::loadImage)
                if (item.second != null) {
                    tvTitleEven.text = item.second?.name
                    tvSubtitleEven.text = "${item.first.countOfItems} deals"
                    item.second?.icon?.let(ivIconEven::loadImage)
                    categorySecond.visible()
                } else {
                    categorySecond.invisible()
                }
                val subcategoryAdapter = SubcategoryAdapter {
                    onSubcategoryClickListener(Pair(it.first, it.second))
                }
                rvSubcategories.adapter = subcategoryAdapter

                categoryFirst.setOnClickListener {
                    subcategoryAdapter.submitList(item.first.subcategory)
                    createFadeTransition(root, grSubcategories)
                }
                categorySecond.setOnClickListener {
                    subcategoryAdapter.submitList(item.second?.subcategory)
                    createFadeTransition(root, grSubcategories)
                }
                ivArrow.setOnClickListener {
                    createFadeTransition(root, grSubcategories, true)
                    notifyDataSetChanged()
                }
            }
        }

        fun unbind() {
            binding.ivIconOdd.setImageDrawable(null)
            binding.ivIconEven.setImageDrawable(null)
            binding.grSubcategories.gone()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Pair<Category, Category?>>() {
        override fun areItemsTheSame(oldItem: Pair<Category, Category?>, newItem: Pair<Category, Category?>): Boolean =
            oldItem.first.id == newItem.first.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: Pair<Category, Category?>,
            newItem: Pair<Category, Category?>
        ): Boolean = oldItem == newItem
    }
}