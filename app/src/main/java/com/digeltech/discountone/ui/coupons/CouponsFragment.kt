package com.digeltech.discountone.ui.coupons

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
import com.digeltech.discountone.databinding.FragmentCouponsBinding
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
class CouponsFragment : BaseFragment(R.layout.fragment_coupons), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentCouponsBinding::bind)
    override val viewModel: CouponsViewModel by viewModels()

    @Inject
    lateinit var logger: AppEventsLogger

    private val args: CouponsFragmentArgs by navArgs()

    private lateinit var couponAdapter: GridDealAdapter
    private lateinit var searchAdapter: GridDealAdapter

    /**
    if args.slug.isEmpty -> dealsRepository.getDiscounts()
    if not -> dealsRepository.getDiscounts() for list of shop and categories,
    then categorySpinner selection with args.slug
    then dealsRepository.getSortingDeals with categorySpinner.position
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initDeals(args.slug)

        loadProfileImage()
        initAdapters()
        initListeners()
        observeData()
        binding.ivLoading.loadGif()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.rvSearchDeals.invisible()
            binding.rvDeals.visible()
            binding.tvSearchResultEmpty.invisible()
            binding.tvTitle.visible()
        } else {
            logSearch(newText.toString(), logger)
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    private fun initAdapters() {
        val stringArray = resources.getStringArray(R.array.fr_coupons_sorting_type)
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, stringArray)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item_dropdown)
        binding.spinnerSortingType.adapter = spinnerAdapter

        couponAdapter = GridDealAdapter(
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
        binding.rvDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
        binding.rvDeals.adapter = couponAdapter

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
    }

    private fun initListeners() {
        binding.searchView.apply {
            setOnClickListener { onActionViewExpanded() }
            setOnQueryTextListener(this@CouponsFragment)
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
            if (it) {
                binding.ivLoading.visible()
            } else {
                binding.ivLoading.invisible()
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
        }
        viewModel.deals.observe(viewLifecycleOwner) {
            couponAdapter.submitList(null)
            if (it.isNotEmpty()) {
                couponAdapter.submitList(it)
                couponAdapter.notifyDataSetChanged()
                binding.tvFilteringResultEmpty.invisible()
                binding.rvDeals.visible()
            } else {
                binding.tvFilteringResultEmpty.visible()
                binding.rvDeals.invisible()
            }
        }
        viewModel.filteringShops.observe(viewLifecycleOwner) {
            val adapterShops = ArrayAdapter(requireContext(), R.layout.spinner_item, getNamesWithFirstAllString(it))
            adapterShops.setDropDownViewResource(R.layout.spinner_item_dropdown)
            binding.spinnerShops.adapter = adapterShops
        }
        viewModel.filteringCategories.observe(viewLifecycleOwner) {
            val adapterCategories =
                ArrayAdapter(requireContext(), R.layout.spinner_item, getNamesWithFirstAllString(it))
            adapterCategories.setDropDownViewResource(R.layout.spinner_item_dropdown)
            binding.spinnerCategories.adapter = adapterCategories
            binding.grFilters.visible()

            if (args.title.isNotEmpty())
                it.forEachIndexed { index, item ->
                    if (item.slug == args.slug)
                        binding.spinnerCategories.setSelection(index + 1)
                }
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