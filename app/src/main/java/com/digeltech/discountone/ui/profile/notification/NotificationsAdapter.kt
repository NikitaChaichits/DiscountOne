package com.digeltech.discountone.ui.profile.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.digeltech.discountone.R
import com.digeltech.discountone.databinding.RvNotificationBinding
import com.digeltech.discountone.domain.model.Notification
import com.digeltech.discountone.util.view.invisible

class NotificationsAdapter(
    private val notifications: List<Notification>,
    private val onClickListener: (notification: Notification) -> Unit,
) : Adapter<NotificationsAdapter.ItemViewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return RvNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemViewholder)
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    inner class ItemViewholder(val binding: RvNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.apply {
                tvTitle.text = notification.title
                tvText.text = notification.text
                tvDate.text = notification.date

                if (notification.isRead) {
                    ivDot.invisible()
                    tvTitle.setTextColor(ContextCompat.getColor(root.context, R.color.light_description))
                    tvText.setTextColor(ContextCompat.getColor(root.context, R.color.light_description))
                } else {
                    root.setOnClickListener { onClickListener(notification) }
                }
            }
        }
    }

    override fun getItemCount(): Int = notifications.size
}