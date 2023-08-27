package com.digeltech.discountone.ui.shops

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentShopsBinding
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.logOpenShopDeals
import com.digeltech.discountone.ui.home.HomeFragmentDirections
import com.digeltech.discountone.ui.shops.adapter.ShopAdapter
import com.digeltech.discountone.util.view.*
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShopsFragment : BaseFragment(R.layout.fragment_shops), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentShopsBinding::bind)

    override val viewModel: ShopsViewModel by viewModels()

    @Inject
    lateinit var logger: AppEventsLogger

    private lateinit var shopAdapter: ShopAdapter
    private lateinit var searchDealAdapter: GridDealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initListeners()

        viewModel.getShopsList()

        loadProfileImage()
        observeData()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.rvSearchDeals.invisible()
            binding.rvShops.visible()
            binding.tvSearchResultEmpty.invisible()
            binding.tvTitle.visible()
        } else {
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    private fun initAdapter() {
        shopAdapter = ShopAdapter {
            navigate(
                ShopsFragmentDirections.toShopFragment(
                    id = it.id,
                    title = it.name,
                    slug = it.slug
                )
            )
            logOpenShopDeals(it.name, requireContext(), logger)
        }
        binding.rvShops.adapter = shopAdapter
        binding.rvShops.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )

        searchDealAdapter = GridDealAdapter(
            {
                navigate(HomeFragmentDirections.toDealFragment(it))
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
        binding.rvSearchDeals.adapter = searchDealAdapter
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
            setOnQueryTextListener(this@ShopsFragment)
            queryHint = getString(R.string.search_by_deals)
        }
    }

    private fun loadProfileImage() {
        Firebase.auth.currentUser?.photoUrl?.let {
            binding.ivProfile.setCircleImage(it)
        }
    }

    private fun observeData() {
        viewModel.shops.observe(viewLifecycleOwner, shopAdapter::submitList)
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
            } else {
                binding.tvSearchResultEmpty.invisible()
            }
            searchDealAdapter.submitList(it)
            binding.rvSearchDeals.visible()
            binding.tvTitle.invisible()
            binding.rvShops.invisible()
        }
    }
}