package com.digeltech.appdiscountone.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentHomeBinding
import com.digeltech.appdiscountone.ui.common.adapter.GridDealAdapter
import com.digeltech.appdiscountone.ui.common.logSearch
import com.digeltech.appdiscountone.ui.home.adapter.BannerAdapter
import com.digeltech.appdiscountone.ui.home.adapter.CategoriesAdapter
import com.digeltech.appdiscountone.util.view.*
import com.digeltech.appdiscountone.util.view.recycler.AutoScrollHelper
import com.digeltech.appdiscountone.util.view.recycler.CyclicScrollHelper
import com.digeltech.appdiscountone.util.view.recycler.GridOffsetDecoration
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    override val viewModel: HomeViewModel by viewModels()

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var searchDealAdapter: GridDealAdapter
    private lateinit var autoScrollHelper: AutoScrollHelper
    private lateinit var cyclicScrollHelper: CyclicScrollHelper


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileImage()
        viewModel.getHomepageData()

        initAdapters()
        initListeners()

        observeData()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.homeGroup.visible()
            binding.tvSearchResultEmpty.invisible()
            binding.rvDeals.invisible()
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
            if (Firebase.auth.currentUser == null) {
                navigate(R.id.startFragment)
            } else {
                navigate(R.id.profileFragment)
            }
        }
        binding.searchView.apply {
            setOnClickListener { onActionViewExpanded() }
            setOnQueryTextListener(this@HomeFragment)
            queryHint = getString(R.string.search_by_deals)
        }
    }

    private fun initAdapters() {
        bannerAdapter = BannerAdapter {
            viewModel.getDeal(dealId = it.first, categoryId = it.second)
        }
        binding.rvBanners.adapter = bannerAdapter

        autoScrollHelper = AutoScrollHelper(binding.rvBanners)
        cyclicScrollHelper = CyclicScrollHelper()
        cyclicScrollHelper.enableCyclicScroll(binding.rvBanners)
        autoScrollHelper.startAutoScroll()

        categoriesAdapter = CategoriesAdapter(
            { navigate(HomeFragmentDirections.toCategoryFragment(id = it.id, title = it.name)) },
            { navigate(HomeFragmentDirections.toDealFragment(it)) }
        )
        binding.rvCategories.adapter = categoriesAdapter

        searchDealAdapter = GridDealAdapter {
            navigate(HomeFragmentDirections.toDealFragment(it))
        }
        binding.rvDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
        binding.rvDeals.adapter = searchDealAdapter
    }

    private fun loadProfileImage() {
        Firebase.auth.currentUser?.photoUrl?.let {
            binding.ivProfile.setCircleImage(it)
        }
    }

    private fun observeData() {
        viewModel.banners.observe(viewLifecycleOwner, bannerAdapter::submitList)
        viewModel.soloBanner.observe(viewLifecycleOwner) { banner ->
            banner?.let {
                binding.ivBannerSale.apply {
                    setImageWithRadius(banner.urlImage, R.dimen.radius_16)
                    setOnClickListener {
                        viewModel.getDeal(dealId = banner.dealId, categoryId = banner.categoryId)
                    }
                    visible()
                }

            }
        }
        viewModel.categories.observe(viewLifecycleOwner, categoriesAdapter::submitList)
        viewModel.deal.observe(viewLifecycleOwner) { deal ->
            deal?.let {
                viewModel.deleteDeal()
                navigate(HomeFragmentDirections.toDealFragment(deal))
            }
        }
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (binding.searchView.query.isNullOrEmpty()) {
                binding.homeGroup.visible()
                binding.tvSearchResultEmpty.invisible()
                binding.rvDeals.invisible()
            } else if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
                binding.homeGroup.invisible()
                binding.rvDeals.invisible()
            } else {
                binding.tvSearchResultEmpty.invisible()
                binding.rvDeals.visible()
                binding.homeGroup.invisible()
            }
            searchDealAdapter.submitList(it)
        }
    }
}