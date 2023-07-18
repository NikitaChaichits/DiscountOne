package com.digeltech.discountone.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentHomeBinding
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.adapter.LinearDealAdapter
import com.digeltech.discountone.ui.common.logSearch
import com.digeltech.discountone.ui.home.adapter.BannerAdapter
import com.digeltech.discountone.ui.home.adapter.CategoriesAdapter
import com.digeltech.discountone.util.view.*
import com.digeltech.discountone.util.view.recycler.AutoScrollHelper
import com.digeltech.discountone.util.view.recycler.CyclicScrollHelper
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    override val viewModel: HomeViewModel by viewModels()

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var bestDealsAdapter: LinearDealAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var searchDealAdapter: GridDealAdapter
    private lateinit var autoScrollHelper: AutoScrollHelper
    private lateinit var cyclicScrollHelper: CyclicScrollHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileImage()

        initAdapters()
        initListeners()

        binding.ivLoading.loadGif()
        observeData()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.homeGroup.visible()
            binding.tvSearchResultEmpty.invisible()
            binding.rvSearchDeals.invisible()
        } else {
            logSearch(newText.toString())
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        autoScrollHelper.stopAutoScroll()
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
        binding.tvMoreBestDeals.setOnClickListener {
            navigate(R.id.dealsFragment)
        }
    }

    private fun initAdapters() {
        bannerAdapter = BannerAdapter {
            viewModel.updateDealViewsClick(it.id.toString())
            navigate(HomeFragmentDirections.toDealFragment(it))
        }
        binding.rvBanners.adapter = bannerAdapter

        autoScrollHelper = AutoScrollHelper(binding.rvBanners)
        cyclicScrollHelper = CyclicScrollHelper()
        cyclicScrollHelper.enableCyclicScroll(binding.rvBanners)
        autoScrollHelper.startAutoScroll()

        bestDealsAdapter = LinearDealAdapter {
            viewModel.updateDealViewsClick(it.id.toString())
            navigate(HomeFragmentDirections.toDealFragment(it))
        }
        binding.rvBestDeals.adapter = bestDealsAdapter

        categoriesAdapter = CategoriesAdapter(
            { navigate(HomeFragmentDirections.toCategoryFragment(id = it.id, title = it.name)) },
            {
                viewModel.updateDealViewsClick(it.id.toString())
                navigate(HomeFragmentDirections.toDealFragment(it))
            }
        )
        binding.rvCategories.adapter = categoriesAdapter

        searchDealAdapter = GridDealAdapter {
            navigate(HomeFragmentDirections.toDealFragment(it))
        }
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
        Firebase.auth.currentUser?.photoUrl?.let {
            binding.ivProfile.setCircleImage(it)
        }
    }

    private fun observeData() {
        viewModel.banners.observe(viewLifecycleOwner, bannerAdapter::submitList)
        viewModel.loadingGifVisibility.observe(viewLifecycleOwner) {
            if (it)
                binding.ivLoading.visible()
            else
                binding.ivLoading.invisible()
        }
//        viewModel.soloBanner.observe(viewLifecycleOwner) { banner ->
//            banner?.let {
//                binding.ivBannerSale.apply {
//                    setImageWithRadius(banner.urlImage, R.dimen.radius_16)
//                    setOnClickListener {
//                        viewModel.getDeal(dealId = banner.dealId, categoryId = banner.categoryId)
//                    }
//                    visible()
//                }
//
//            }
//        }
        viewModel.bestDeals.observe(viewLifecycleOwner, bestDealsAdapter::submitList)
        viewModel.categories.observe(viewLifecycleOwner, categoriesAdapter::submitList)
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (binding.searchView.query.isNullOrEmpty()) {
                binding.homeGroup.visible()
                binding.tvSearchResultEmpty.invisible()
                binding.rvSearchDeals.invisible()
            } else if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
                binding.homeGroup.invisible()
                binding.rvSearchDeals.invisible()
            } else {
                binding.tvSearchResultEmpty.invisible()
                binding.homeGroup.gone()
                binding.rvSearchDeals.visible()
            }
            searchDealAdapter.submitList(it)
            binding.ivLoading.invisible()
        }
    }
}