package com.digeltech.discountone.ui.categories.categoryandshop

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentCategoryAndShopBinding
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.logSearch
import com.digeltech.discountone.util.view.gone
import com.digeltech.discountone.util.view.invisible
import com.digeltech.discountone.util.view.px
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.digeltech.discountone.util.view.visible
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoryAndShopFragment : BaseFragment(R.layout.fragment_category_and_shop), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentCategoryAndShopBinding::bind)

    override val viewModel: CategoryAndShopViewModel by viewModels()

    private val args: CategoryAndShopFragmentArgs by navArgs()

    @Inject
    lateinit var logger: AppEventsLogger

    private lateinit var dealAdapter: GridDealAdapter
    private lateinit var searchAdapter: GridDealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTitle.text = args.title
        /**
         * isFromCategory default value = true
         */
        binding.tvSortingCatOrShop.text = if (args.isFromCategory) getString(R.string.fr_deals_filter_shops)
        else getString(R.string.fr_deals_filter_categories)
        viewModel.initScreenData(args.slug, args.isFromCategory)

        observeData()
        initAdapters()
        initListeners()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.tvTitle.visible()
            binding.rvSearchDeals.invisible()
            binding.grFilters.visible()
            if (binding.rvDeals.adapter?.itemCount == 0) {
                binding.tvFilteringResultEmpty.visible()
            } else {
                binding.rvDeals.visible()
            }
            binding.tvSearchResultEmpty.invisible()
        } else {
            logSearch(newText.toString(), logger)
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    private fun initAdapters() {
        dealAdapter = GridDealAdapter(
            {
//                viewModel.updateDealViewsClick(it.id.toString())
                navigate(CategoryAndShopFragmentDirections.toDealFragment(it))
            },
            logger
        )
        binding.rvDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
        binding.rvDeals.adapter = dealAdapter

        searchAdapter = GridDealAdapter(
            {
//                viewModel.updateDealViewsClick(it.id.toString())
                navigate(CategoryAndShopFragmentDirections.toDealFragment(it))
                binding.searchView.setQuery("", false)
            },
            logger
        )
        binding.rvSearchDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
        binding.rvSearchDeals.adapter = searchAdapter

        val stringArray = resources.getStringArray(R.array.fr_deals_sorting_type)
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, stringArray)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        binding.spinnerSortingType.adapter = spinnerAdapter
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.searchView.apply {
            setOnClickListener { onActionViewExpanded() }
            setOnQueryTextListener(this@CategoryAndShopFragment)
            queryHint = getString(R.string.search_by_deals)
        }
        binding.spinnerSortingType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == viewModel.getSortBySpinnerPosition()) {
                    return
                }
                viewModel.sortingByType(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spinnerSortingCatOrShop.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == viewModel.getCatOrShopSpinnerPosition()) {
                    return
                }
                viewModel.sortingByCatOrShop(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.tvSortingPrice.setOnClickListener {
            val bottomSheetFragment = BottomSheetPriceFragment(viewModel.getPriceFrom(), viewModel.getPriceTo())
            bottomSheetFragment.setBottomSheetListener(object : BottomSheetPriceFragment.BottomSheetListener {
                override fun onSubmitClicked(input1: Int, input2: Int) {
                    viewModel.sortingByPrice(input1, input2)
                }
            })
            bottomSheetFragment.show(requireActivity().supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    private fun observeData() {
        viewModel.categoryOrShopNames.observe(viewLifecycleOwner) {
            val list = mutableListOf("All")
            it.forEach { item ->
                list.add(item.name)
            }
            val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, list)
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
            binding.spinnerSortingCatOrShop.adapter = spinnerAdapter
        }
        viewModel.deals.observe(viewLifecycleOwner) {
            dealAdapter.submitList(null)
            if (it.isNotEmpty()) {
                dealAdapter.submitList(it)
                binding.tvFilteringResultEmpty.invisible()
                binding.rvDeals.visible()
            } else {
                binding.tvFilteringResultEmpty.visible()
                binding.rvDeals.invisible()
            }
        }
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
                binding.tvTitle.invisible()
            } else {
                binding.tvSearchResultEmpty.invisible()
                binding.tvTitle.visible()
            }
            searchAdapter.submitList(it)
            binding.rvSearchDeals.visible()
            binding.rvDeals.invisible()
            binding.grFilters.gone()
            binding.tvFilteringResultEmpty.invisible()
        }
        viewModel.filteringError.observe(viewLifecycleOwner) {
            binding.tvFilteringResultEmpty.visible()
            binding.rvDeals.invisible()
        }
    }
}