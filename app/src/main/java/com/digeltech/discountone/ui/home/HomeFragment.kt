package com.digeltech.discountone.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentHomeBinding
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.adapter.LinearDealAdapter
import com.digeltech.discountone.ui.common.model.DealType
import com.digeltech.discountone.ui.home.adapter.BannerAdapter
import com.digeltech.discountone.ui.home.adapter.CategoriesAdapter
import com.digeltech.discountone.ui.home.adapter.HomeShopsAdapter
import com.digeltech.discountone.util.logSearch
import com.digeltech.discountone.util.view.*
import com.digeltech.discountone.util.view.recycler.AutoScrollHelper
import com.digeltech.discountone.util.view.recycler.CyclicScrollHelper
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.facebook.appevents.AppEventsLogger
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    override val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var logger: AppEventsLogger

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var couponsLinearAdapter: LinearDealAdapter
    private lateinit var discountsLinearAdapter: LinearDealAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var shopsAdapter: HomeShopsAdapter

    private lateinit var searchDealAdapter: GridDealAdapter
    private lateinit var bannerAutoScrollHelper: AutoScrollHelper
    private lateinit var bannerCyclicScrollHelper: CyclicScrollHelper
    private lateinit var shopsAutoScrollHelper: AutoScrollHelper
    private lateinit var shopsCyclicScrollHelper: CyclicScrollHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getHomepageData()
        loadProfileImage()

        initAdapters()
        initListeners()
        observeData()

        binding.ivLoading.loadGif()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.mainContentGroup.visible()
            binding.tvSearchResultEmpty.invisible()
            binding.rvSearchDeals.invisible()
        } else {
            logSearch(newText.toString(), logger)
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        bannerAutoScrollHelper.stopAutoScroll()
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
            setOnQueryTextListener(this@HomeFragment)
            queryHint = getString(R.string.search_by_deals)
        }
        binding.tvMoreDiscounts.setOnClickListener {
            navigate(R.id.discountsFragment)
        }
        binding.tvMoreCoupons.setOnClickListener {
            navigate(R.id.couponsFragment)
        }
        binding.ivArrowBackward.setOnClickListener {
            val layoutManager = binding.rvShops.layoutManager as? LinearLayoutManager
            val currentPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
            if (currentPosition != 0)
                binding.rvShops.smoothScrollToPosition(currentPosition - 1)
        }
        binding.ivArrowForward.setOnClickListener {
            val layoutManager = binding.rvShops.layoutManager as? LinearLayoutManager
            val currentPosition = layoutManager?.findLastVisibleItemPosition() ?: 0
            binding.rvShops.smoothScrollToPosition(currentPosition + 1)
        }
    }

    private fun initAdapters() {
        // scrolling horizontal RV for banners
        bannerAdapter = BannerAdapter {
            navigate(HomeFragmentDirections.toDealFragment(it))
        }
        binding.rvBanners.adapter = bannerAdapter

        bannerCyclicScrollHelper = CyclicScrollHelper()
        bannerCyclicScrollHelper.enableBannerCyclicScroll(binding.rvBanners)
        bannerAutoScrollHelper = AutoScrollHelper(binding.rvBanners)
        bannerAutoScrollHelper.startAutoScroll()

        shopsCyclicScrollHelper = CyclicScrollHelper()
        shopsCyclicScrollHelper.enableShopCyclicScroll(binding.rvShops)
        shopsAutoScrollHelper = AutoScrollHelper(binding.rvShops)
        shopsAutoScrollHelper.startShopsAutoScroll()

        // Linear horizontal RV for discounts
        discountsLinearAdapter = LinearDealAdapter(
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
        binding.rvDiscounts.adapter = discountsLinearAdapter

        // Linear horizontal RV for coupons
        couponsLinearAdapter = LinearDealAdapter(
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
        binding.rvCoupons.adapter = couponsLinearAdapter

        // Linear horizontal RV for shops
        shopsAdapter = HomeShopsAdapter(
            onClickListener = {
                navigate(HomeFragmentDirections.toShopFragment(it.id, it.name, it.slug))
            }
        )
        binding.rvShops.adapter = shopsAdapter

        // Linear vertical RV for Categories with subcategories with Linear horizontal RV for deals
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        categoriesAdapter = CategoriesAdapter(
            onMoreDealsClick = {
                if (it.items.first().dealType == DealType.COUPONS)
                    navigate(HomeFragmentDirections.toCouponsFragment(title = it.name, slug = it.slug))
                else navigate(HomeFragmentDirections.toDiscountsFragment(title = it.name, slug = it.slug))
            },
            onDealClick = {
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
        binding.rvCategories.apply {
            adapter = categoriesAdapter
            this.layoutManager = layoutManager
        }

        //Grid RV for searching results
        searchDealAdapter = GridDealAdapter(
            onClickListener = {
                navigate(HomeFragmentDirections.toDealFragment(it))
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
        binding.rvSearchDeals.adapter = searchDealAdapter
    }

    private fun loadProfileImage() {
        Hawk.get<User>(KEY_USER)?.let {
            it.avatarUrl?.let { url ->
                binding.ivProfile.setProfileImage(url)
            }
        }
    }

    private fun observeData() {
        viewModel.loadingGifVisibility.observe(viewLifecycleOwner) {
            if (it)
                binding.ivLoading.visible()
            else
                binding.ivLoading.invisible()
        }
        viewModel.banners.observe(viewLifecycleOwner, bannerAdapter::submitList)
        viewModel.discounts.observe(viewLifecycleOwner, discountsLinearAdapter::submitList)
        viewModel.coupons.observe(viewLifecycleOwner, couponsLinearAdapter::submitList)
        viewModel.shops.observe(viewLifecycleOwner) {
            shopsAdapter.submitList(it)
        }
        viewModel.categories.observe(viewLifecycleOwner) {
            categoriesAdapter.submitList(it)
            binding.mainContentGroup.visible()
        }
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (binding.searchView.query.isNullOrEmpty()) {
                binding.mainContentGroup.visible()
                binding.tvSearchResultEmpty.invisible()
                binding.rvSearchDeals.invisible()
            } else if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
                binding.mainContentGroup.invisible()
                binding.rvSearchDeals.invisible()
            } else {
                binding.tvSearchResultEmpty.invisible()
                binding.mainContentGroup.gone()
                binding.rvSearchDeals.visible()
            }
            searchDealAdapter.submitList(it)
            binding.ivLoading.invisible()
        }
    }
}