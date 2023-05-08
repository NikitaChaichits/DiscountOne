package com.digeltech.appdiscountone.ui.categories.categoryandshop

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentCategoryAndShopBinding
import com.digeltech.appdiscountone.ui.common.adapter.GridDealAdapter
import com.digeltech.appdiscountone.util.view.getString
import com.digeltech.appdiscountone.util.view.invisible
import com.digeltech.appdiscountone.util.view.px
import com.digeltech.appdiscountone.util.view.recycler.GridOffsetDecoration
import com.digeltech.appdiscountone.util.view.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryAndShopFragment : BaseFragment(R.layout.fragment_category_and_shop), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentCategoryAndShopBinding::bind)

    override val viewModel: CategoryAndShopViewModel by viewModels()

    private val args: CategoryAndShopFragmentArgs by navArgs()

    private lateinit var dealAdapter: GridDealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        initListeners()

        binding.tvTitle.text = args.title
        viewModel.initDeals(args.id)

        observeData()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.searchDeals(newText.toString())
        return true
    }

    private fun initAdapters() {
        dealAdapter = GridDealAdapter {
            navigate(CategoryAndShopFragmentDirections.toDealFragment(it))
        }
        binding.rvDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
        binding.rvDeals.adapter = dealAdapter
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.searchView.apply {
            setOnQueryTextListener(this@CategoryAndShopFragment)
            queryHint = getString(R.string.search_by_deals)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deals.collect(dealAdapter::submitList)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult.collect {
                if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                    binding.tvSearchResultEmpty.visible()
                    binding.tvTitle.invisible()
                } else {
                    binding.tvSearchResultEmpty.invisible()
                    binding.tvTitle.visible()
                }
                dealAdapter.submitList(it)
            }
        }
    }

}