package com.digeltech.discountone.ui.discounts

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentDiscountsBinding
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.loadProfileImage
import com.digeltech.discountone.util.isNotNullAndNotEmpty
import com.digeltech.discountone.util.logSearch
import com.digeltech.discountone.util.view.categoriesStyledAdapter
import com.digeltech.discountone.util.view.getNamesWithFirstAllString
import com.digeltech.discountone.util.view.getString
import com.digeltech.discountone.util.view.gone
import com.digeltech.discountone.util.view.invisible
import com.digeltech.discountone.util.view.loadGif
import com.digeltech.discountone.util.view.px
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.digeltech.discountone.util.view.visible
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DiscountsFragment : BaseFragment(R.layout.fragment_discounts), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentDiscountsBinding::bind)

    override val viewModel: DiscountsViewModel by viewModels()

    @Inject
    lateinit var logger: AppEventsLogger

    private val args: DiscountsFragmentArgs by navArgs()

    private lateinit var dealAdapter: GridDealAdapter
    private lateinit var searchAdapter: GridDealAdapter

    private var searchText: String? = null

    /**
    if args.slug.isEmpty -> dealsRepository.getDiscounts()
    if not -> dealsRepository.getDiscounts() for list of shop and categories,
    then categorySpinner selection with args.slug
    then dealsRepository.getSortingDeals with categorySpinner.position
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initDeals(args.slug)

        initAdapters()
        initListeners()
        observeData()

        loadProfileImage(binding.ivProfile)
        binding.ivLoading.loadGif()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.tvTitle.visible()
            binding.grFilters.visible()
            binding.rvDeals.visible()
            binding.rvSearchDeals.gone()
            binding.tvSearchResultEmpty.invisible()
            searchText = null
        } else {
            if (newText == searchText) return false
            logSearch(newText.toString(), logger)
            searchText = newText
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentCategorySpinnerPosition.let {
            if (it > 0) {
                binding.spinnerCategories.setSelection(it)
            }
        }
        viewModel.currentSortBySpinnerPosition.let {
            if (it > 0) {
                binding.spinnerSortingType.setSelection(it)
            }
        }
        viewModel.currentShopSpinnerPosition.let {
            if (it > 0) {
                binding.spinnerShops.setSelection(it)
            }
        }
    }

    private fun initAdapters() {
        dealAdapter = GridDealAdapter(
            onClickListener = {
                viewModel.updateDealViewsClick(it.id.toString())
                navigateToDealFragment(it)
            },
            onBookmarkClickListener = { viewModel.updateBookmark(it.toString()) },
            fragmentManager = requireActivity().supportFragmentManager,
            logger = logger,
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
            onClickListener = {
                viewModel.updateDealViewsClick(it.id.toString())
                navigateToDealFragment(it)
            },
            onBookmarkClickListener = { viewModel.updateBookmark(it.toString()) },
            fragmentManager = requireActivity().supportFragmentManager,
            logger = logger,
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
        binding.searchView.apply {
            setOnClickListener { onActionViewExpanded() }
            setOnQueryTextListener(this@DiscountsFragment)
            queryHint = getString(R.string.search_by_deals)
        }
        binding.ivProfile.setOnClickListener {
            if (prefs.isLogin()) {
                navigate(R.id.profileFragment)
            } else {
                navigate(R.id.startFragment)
            }
        }
        binding.spinnerSortingType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == viewModel.currentSortBySpinnerPosition) {
                    return
                }
                viewModel.sortingByType(position, viewModel::getFilteringDeals)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == viewModel.currentCategorySpinnerPosition) {
                    return
                }
                viewModel.sortingByCategory(position, viewModel::getFilteringDeals)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spinnerShops.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == viewModel.currentShopSpinnerPosition) {
                    return
                }
                viewModel.sortingByShop(position, viewModel::getFilteringDeals)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        with(binding.nsvContent) {
            val scrollListener = ViewTreeObserver.OnScrollChangedListener {
                val scrollY = this.scrollY
                val totalHeight = this.getChildAt(0).height
                val currentHeight = this.height

                if (scrollY + currentHeight >= totalHeight && scrollY != 0) {
                    viewModel.loadMoreDeals()
                }
            }
            this.viewTreeObserver.addOnScrollChangedListener(scrollListener)
        }
    }

    private fun observeData() {
        viewModel.loadingGifVisibility.observe(viewLifecycleOwner) {
            if (it)
                binding.ivLoading.visible()
            else
                binding.ivLoading.invisible()
        }
        viewModel.deals.observe(viewLifecycleOwner) {
            dealAdapter.submitList(null)
            if (it.isNotEmpty()) {
                dealAdapter.submitList(it)
                dealAdapter.notifyDataSetChanged()
                binding.rvDeals.visible()
                viewModel.filteringError.value = null
                binding.tvFilteringResultEmpty.invisible()
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
            binding.rvDeals.gone()
            binding.grFilters.gone()
            binding.tvFilteringResultEmpty.invisible()
        }
        viewModel.filteringShops.observe(viewLifecycleOwner) {
            val adapterShops = ArrayAdapter(requireContext(), R.layout.spinner_item, getNamesWithFirstAllString(it))
            adapterShops.setDropDownViewResource(R.layout.spinner_item_dropdown)
            binding.spinnerShops.adapter = adapterShops
        }
        viewModel.filteringCategories.observe(viewLifecycleOwner) {
            val adapterCategories = categoriesStyledAdapter(requireContext(), it)
            adapterCategories.setDropDownViewResource(R.layout.spinner_item_dropdown)
            binding.spinnerCategories.adapter = adapterCategories

            if (args.title.isNotEmpty())
                it.forEachIndexed { index, item ->
                    if (item.slug == args.slug)
                        binding.spinnerCategories.setSelection(index + 1)
                }
        }
        viewModel.filteringError.observe(viewLifecycleOwner) {
            if (it.isNotNullAndNotEmpty()) {
                binding.tvFilteringResultEmpty.visible()
                binding.rvDeals.invisible()
            }
        }
    }
}