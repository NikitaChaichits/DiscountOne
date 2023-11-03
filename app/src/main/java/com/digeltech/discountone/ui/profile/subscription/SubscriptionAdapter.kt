package com.digeltech.discountone.ui.profile.subscription

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.digeltech.discountone.databinding.RvSubscriptionCategoryBinding
import com.digeltech.discountone.domain.model.SubscriptionCategory
import com.digeltech.discountone.util.view.invisible
import com.digeltech.discountone.util.view.loadImage
import com.digeltech.discountone.util.view.visible

class SubscriptionAdapter(
    private val onSubcategoryClickListener: (categoryId: Int) -> Unit,
) : ListAdapter<SubscriptionCategory, SubscriptionAdapter.ItemViewholder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvSubscriptionCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) =
        holder.bind(getItem(position))

    override fun onViewRecycled(holder: ItemViewholder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ItemViewholder(val binding: RvSubscriptionCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SubscriptionCategory) {
            with(binding) {
                tvTitle.text = item.name
                item.iconUrl.let(ivIcon::loadImage)

                if (item.isNotificationOff) ivNotificationOn.invisible()
                else ivNotificationOn.visible()

                root.setOnClickListener {
                    if (ivNotificationOn.isVisible) ivNotificationOn.invisible()
                    else ivNotificationOn.visible()

                    onSubcategoryClickListener(item.id)
                }
            }
        }

        fun unbind() {
            binding.ivIcon.setImageDrawable(null)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SubscriptionCategory>() {
        override fun areItemsTheSame(oldItem: SubscriptionCategory, newItem: SubscriptionCategory): Boolean =
            oldItem.id == newItem.id

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: SubscriptionCategory,
            newItem: SubscriptionCategory
        ): Boolean = oldItem == newItem
    }
}