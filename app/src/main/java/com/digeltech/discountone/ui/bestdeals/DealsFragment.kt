package com.digeltech.discountone.ui.bestdeals

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentBestDealsBinding
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.loadProfileImage
import com.digeltech.discountone.ui.common.model.Taxonomy
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
class DealsFragment : BaseFragment(R.layout.fragment_best_deals), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentBestDealsBinding::bind)
    override val viewModel: DealsViewModel by viewModels()

    @Inject
    lateinit var logger: AppEventsLogger

    private lateinit var dealAdapter: GridDealAdapter
    private lateinit var searchAdapter: GridDealAdapter

    private var searchText: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            binding.grContent.visible()
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
            onBookmarkClickListener = {
                viewModel.updateBookmark(it.toString())
            },
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

        searchAdapter = GridDealAdapter(
            onClickListener = {
                viewModel.updateDealViewsClick(it.id.toString())
                navigateToDealFragment(it)
            },
            onBookmarkClickListener = {
                viewModel.updateBookmark(it.toString())
            },
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

        val dealsTypeArray = resources.getStringArray(R.array.fr_deals_type)
        val dealsTypeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, dealsTypeArray)
        dealsTypeAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        binding.spinnerDealType.adapter = dealsTypeAdapter
    }

    private fun initListeners() {
        binding.ivProfile.setOnClickListener {
            if (prefs.isLogin()) {
                navigate(R.id.profileFragment)
            } else {
                navigate(R.id.startFragment)
            }
        }
        binding.searchView.apply {
            setOnClickListener { onActionViewExpanded() }
            setOnQueryTextListener(this@DealsFragment)
            queryHint = getString(R.string.search_by_deals)
        }
        binding.spinnerDealType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == viewModel.currentDealTypeSpinnerPosition) {
                    return
                }
                viewModel.filteringCategories.value?.let {
                    when (position) {
                        1 -> { // DealType.DISCOUNTS chosen
                            //setting only coupons categories for category spinner
                            val filteredCategories = it.filter { item -> item.taxonomy != Taxonomy.COUPONS.type }
                            val adapterCategories = ArrayAdapter(
                                requireContext(),
                                R.layout.spinner_item,
                                getNamesWithFirstAllString(filteredCategories)
                            )
                            adapterCategories.setDropDownViewResource(R.layout.spinner_item_dropdown)
                            binding.spinnerCategories.adapter = adapterCategories
                            viewModel.categorySlug = ""
                            viewModel.currentCategorySpinnerPosition = 0
                        }
                        2 -> { // DealType.COUPONS chosen
                            //setting only discounts categories for category spinner

                            val filteredCategories = it.filter { item -> item.taxonomy == Taxonomy.COUPONS.type }
                            val adapterCategories = ArrayAdapter(
                                requireContext(),
                                R.layout.spinner_item,
                                getNamesWithFirstAllString(filteredCategories)
                            )
                            adapterCategories.setDropDownViewResource(R.layout.spinner_item_dropdown)
                            binding.spinnerCategories.adapter = adapterCategories
                            viewModel.categorySlug = ""
                            viewModel.currentCategorySpinnerPosition = 0
                        }
                        else -> {// DealType.ALL chosen
                            val adapterCategories = categoriesStyledAdapter(requireContext(), it)
                            adapterCategories.setDropDownViewResource(R.layout.spinner_item_dropdown)
                            binding.spinnerCategories.adapter = adapterCategories
                        }
                    }
                }

                viewModel.sortingByDealType(position, viewModel::getFilteringDeals)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spinnerCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == viewModel.currentCategorySpinnerPosition) {
                        return
                    }
                    viewModel.sortingByCategory(position, viewModel::getFilteringDeals)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        binding.spinnerShops.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position == viewModel.currentShopSpinnerPosition) {
                        return
                    }
                    viewModel.sortingByShop(position, viewModel::getFilteringDeals)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
    }

    private fun observeData() {
        viewModel.loadingGifVisibility.observe(viewLifecycleOwner) {
            if (it) {
                binding.ivLoading.visible()
            } else {
                binding.ivLoading.invisible()
            }
        }
        viewModel.deals.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.tvFilteringResultEmpty.visible()
                binding.rvDeals.invisible()
            } else {
                dealAdapter.submitList(it)
                dealAdapter.notifyDataSetChanged()
                binding.grContent.visible()
                binding.rvDeals.visible()
                viewModel.filteringError.value = null
                binding.tvFilteringResultEmpty.invisible()
            }
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
        }
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
                binding.tvTitle.invisible()
                binding.grContent.invisible()
            } else {
                binding.tvSearchResultEmpty.invisible()
                binding.tvTitle.visible()
            }
            searchAdapter.submitList(it)
            binding.rvSearchDeals.visible()
            binding.grContent.gone()
        }
        viewModel.filteringError.observe(viewLifecycleOwner) {
            if (it.isNotNullAndNotEmpty()) {
                binding.tvFilteringResultEmpty.visible()
                binding.rvDeals.invisible()
            }
        }
    }
}