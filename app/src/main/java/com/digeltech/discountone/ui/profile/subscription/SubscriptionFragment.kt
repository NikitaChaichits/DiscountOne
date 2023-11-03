package com.digeltech.discountone.ui.profile.subscription

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentSubscriptionBinding
import com.digeltech.discountone.util.view.getImageDrawable
import com.digeltech.discountone.util.view.px
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.digeltech.discountone.util.view.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SubscriptionFragment : BaseFragment(R.layout.fragment_subscription) {

    private val binding by viewBinding(FragmentSubscriptionBinding::bind)
    override val viewModel: SubscriptionViewModel by viewModels()

    private lateinit var subscriptionAdapter: SubscriptionAdapter
    private var isEmailNotificationOn = true


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initAdapters()
        observeData()
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.ivSwitch.setOnClickListener {
            isEmailNotificationOn = if (isEmailNotificationOn) {
                binding.ivSwitch.setImageDrawable(view?.getImageDrawable(R.drawable.ic_switch_off))
                false
            } else {
                binding.ivSwitch.setImageDrawable(view?.getImageDrawable(R.drawable.ic_switch_on))
                true
            }
        }
        binding.btnSave.setOnClickListener {
            viewModel.updateSubscriptionCategories(
                isEmailNotificationOn = isEmailNotificationOn,
            )
        }
    }

    private fun initAdapters() {
        subscriptionAdapter = SubscriptionAdapter(viewModel::checkCategoriesForUnsubscribe)
        binding.rvSubscriptionCategories.adapter = subscriptionAdapter
        binding.rvSubscriptionCategories.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                toast("Successfully saved")
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    navigateBack()
                }
            }
        }
        viewModel.isEmailNotificationOn.observe(viewLifecycleOwner) {
            isEmailNotificationOn = if (it) {
                binding.ivSwitch.setImageDrawable(view?.getImageDrawable(R.drawable.ic_switch_on))
                false
            } else {
                binding.ivSwitch.setImageDrawable(view?.getImageDrawable(R.drawable.ic_switch_off))
                true
            }
        }
        viewModel.categories.observe(viewLifecycleOwner) {
            subscriptionAdapter.submitList(it)
            binding.nsvContent.visible()
        }
    }

}