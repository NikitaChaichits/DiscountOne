package com.digeltech.appdiscountone.ui.categories.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentCategoryBinding
import com.digeltech.appdiscountone.ui.coupons.adapter.DealAdapter
import com.digeltech.appdiscountone.util.view.px
import com.digeltech.appdiscountone.util.view.recycler.GridOffsetDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : BaseFragment(R.layout.fragment_category) {

    private val binding by viewBinding(FragmentCategoryBinding::bind)

    override val viewModel: CategoryViewModel by viewModels()

    private val args: CategoryFragmentArgs by navArgs()

    private lateinit var dealAdapter: DealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        initListeners()

        binding.tvTitle.text = args.categoryName
        viewModel.getCategoryDeals(args.categoryId)

        observeData()
    }

    private fun initAdapters() {
        dealAdapter = DealAdapter {
            navigate(CategoryFragmentDirections.toDealFragment(it))
        }
        binding.rvDeals.adapter = dealAdapter
        binding.rvDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 0.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            navigateBack()
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deals.collect(dealAdapter::submitList)
        }
    }
}