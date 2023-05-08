package com.digeltech.appdiscountone.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentHomeBinding
import com.digeltech.appdiscountone.ui.common.adapter.GridDealAdapter
import com.digeltech.appdiscountone.ui.home.adapter.BannerAdapter
import com.digeltech.appdiscountone.ui.home.adapter.CategoriesAdapter
import com.digeltech.appdiscountone.util.view.*
import com.digeltech.appdiscountone.util.view.recycler.GridOffsetDecoration
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    override val viewModel: HomeViewModel by viewModels()

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var searchDealAdapter: GridDealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileImage()
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
            viewModel.searchDeals(newText.toString())
        }
        return true
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
            setOnQueryTextListener(this@HomeFragment)
            queryHint = getString(R.string.search_by_deals)
        }
    }

    private fun initAdapters() {
        bannerAdapter = BannerAdapter {
            viewModel.getDeal(dealId = it.first, categoryId = it.second)
        }
        binding.rvBanners.adapter = bannerAdapter

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.banners.collect(bannerAdapter::submitList)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.soloBanner.collect { banner ->
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
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect(categoriesAdapter::submitList)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deal.collect { deal ->
                deal?.let {
                    navigate(HomeFragmentDirections.toDealFragment(it))
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult.collect {
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
}