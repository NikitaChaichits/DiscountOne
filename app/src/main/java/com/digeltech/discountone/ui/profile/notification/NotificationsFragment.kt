package com.digeltech.discountone.ui.profile.notification

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentNotificationsBinding
import com.digeltech.discountone.ui.common.KEY_NOTIFICATION
import com.digeltech.discountone.ui.common.getNotificationsList
import com.digeltech.discountone.ui.common.updateNotification
import com.digeltech.discountone.util.notifications.openNotification
import com.digeltech.discountone.util.time.getCurrentDate
import com.digeltech.discountone.util.view.gone
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : BaseFragment(R.layout.fragment_notifications) {

    private val binding by viewBinding(FragmentNotificationsBinding::bind)
    override val viewModel: NotificationsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        setupNotifications()
    }

    private fun initListeners() {
        binding.apply {
            ivBack.setOnClickListener {
                navigateBack()
            }
            ivDelete.setOnClickListener {
                MaterialDialog(requireContext()).show {
                    title(text = "Clear all notifications?")
                    positiveButton(text = "Yes") {
                        Hawk.delete(KEY_NOTIFICATION)
                        binding.llContent.gone()
                    }
                    negativeButton(text = "Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }

    private fun setupNotifications() {
        val notifications = getNotificationsList()
        val currentDate = getCurrentDate()

        val todayNotifications = notifications.filter { it.date == currentDate }
        if (todayNotifications.isNotEmpty()) {
            NotificationsAdapter(todayNotifications) {
                updateNotification(it)
                openNotification(
                    fragment = this,
                    data = it.data
                )
            }.apply {
                binding.rvTodayNotifications.adapter = this
            }
        } else {
            binding.tvToday.gone()
            binding.rvTodayNotifications.gone()
        }

        val otherNotifications = notifications.filter { it.date != currentDate }
        if (otherNotifications.isNotEmpty()) {
            NotificationsAdapter(otherNotifications) {
                updateNotification(it)
                toast(it.text)
            }.apply {
                binding.rvOtherNotifications.adapter = this
            }
        } else {
            binding.tvOtherDays.gone()
            binding.rvOtherNotifications.gone()
        }
    }

}