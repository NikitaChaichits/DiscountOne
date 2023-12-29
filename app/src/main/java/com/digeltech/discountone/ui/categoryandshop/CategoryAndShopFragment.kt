package com.digeltech.discountone.ui.categoryandshop

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
import com.digeltech.discountone.databinding.FragmentCategoryAndShopBinding
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.model.Taxonomy
import com.digeltech.discountone.util.logSearch
import com.digeltech.discountone.util.view.*
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.facebook.appevents.AppEventsLogger
import com.orhanobut.hawk.Hawk
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

        viewModel.initScreenData(args.slug, args.id.toString())

        loadProfileImage()
        initAdapters()
        initListeners()
        observeData()
        binding.ivLoading.loadGif()
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

        val sortingTypeArray = resources.getStringArray(R.array.fr_coupons_sorting_type)
        val sortingTypeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, sortingTypeArray)
        sortingTypeAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        binding.spinnerSortingType.adapter = sortingTypeAdapter

        val dealsTypeArray = resources.getStringArray(R.array.fr_deals_type)
        val dealsTypeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, dealsTypeArray)
        dealsTypeAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        binding.spinnerDealType.adapter = dealsTypeAdapter
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
        binding.ivProfile.setOnClickListener {
            if (prefs.isLogin()) {
                navigate(R.id.profileFragment)
            } else {
                navigate(R.id.startFragment)
            }
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
    }

    private fun observeData() {
        viewModel.loadingGifVisibility.observe(viewLifecycleOwner) {
            if (it) {
                binding.ivLoading.visible()
            } else {
                binding.ivLoading.invisible()
            }
        }
        viewModel.filteringCategories.observe(viewLifecycleOwner) {
            val adapterCategories = categoriesStyledAdapter(requireContext(), it)
            adapterCategories.setDropDownViewResource(R.layout.spinner_item_dropdown)
            binding.spinnerCategories.adapter = adapterCategories
        }
        viewModel.deals.observe(viewLifecycleOwner) {
            dealAdapter.submitList(null)
            if (it.isNotEmpty()) {
                dealAdapter.submitList(it)
                dealAdapter.notifyDataSetChanged()
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

    private fun loadProfileImage() {
        Hawk.get<User>(KEY_USER)?.let {
            it.avatarUrl?.let { url ->
                binding.ivProfile.setProfileImage(url)
            }
        }
    }
}