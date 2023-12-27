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
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.util.logSearch
import com.digeltech.discountone.util.view.*
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.facebook.appevents.AppEventsLogger
import com.orhanobut.hawk.Hawk
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        initListeners()
        loadProfileImage()
        observeData()

        binding.ivLoading.loadGif()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.rvSearchDeals.invisible()
            binding.tvSearchResultEmpty.invisible()
            binding.tvTitle.visible()
            binding.grContent.visible()
        } else {
            logSearch(newText.toString(), logger)
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

    private fun loadProfileImage() {
        Hawk.get<User>(KEY_USER)?.let {
            it.avatarUrl?.let { url ->
                binding.ivProfile.setProfileImage(url)
            }
        }
    }

    private fun initAdapters() {
        dealAdapter = GridDealAdapter(
            onClickListener = {
                val bundle = Bundle().apply {
                    putParcelable("deal", it)
                }
                navigate(R.id.dealFragment, bundle)
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

                if (scrollY + currentHeight >= totalHeight) {
                    viewModel.loadMoreDeals()
                }
            }
            this.viewTreeObserver.addOnScrollChangedListener(scrollListener)
        }

        searchAdapter = GridDealAdapter(
            onClickListener = {
                val bundle = Bundle().apply {
                    putParcelable("deal", it)
                }
                navigate(R.id.dealFragment, bundle)
                binding.searchView.setQuery("", false)
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
                            val filteredCategories = it.filter { item -> item.taxonomy != "categories-coupons" }
                            val adapterCategories = ArrayAdapter(
                                requireContext(),
                                R.layout.spinner_item,
                                getNamesWithFirstAllString(filteredCategories)
                            )
                            adapterCategories.setDropDownViewResource(R.layout.spinner_item_dropdown)
                            binding.spinnerCategories.adapter = adapterCategories
                        }
                        2 -> { // DealType.COUPONS chosen
                            //setting only discounts categories for category spinner

                            val filteredCategories = it.filter { item -> item.taxonomy == "categories-coupons" }
                            val adapterCategories = ArrayAdapter(
                                requireContext(),
                                R.layout.spinner_item,
                                getNamesWithFirstAllString(filteredCategories)
                            )
                            adapterCategories.setDropDownViewResource(R.layout.spinner_item_dropdown)
                            binding.spinnerCategories.adapter = adapterCategories
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
                binding.tvFilteringResultEmpty.invisible()
                binding.grContent.visible()
                binding.rvDeals.visible()
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
            binding.grContent.invisible()
        }
        viewModel.filteringError.observe(viewLifecycleOwner) {
            binding.tvFilteringResultEmpty.visible()
            binding.rvDeals.invisible()
        }
    }
}