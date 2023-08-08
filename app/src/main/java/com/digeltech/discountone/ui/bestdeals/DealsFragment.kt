package com.digeltech.discountone.ui.bestdeals

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentBestDealsBinding
import com.digeltech.discountone.domain.model.Item
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.logSearch
import com.digeltech.discountone.ui.coupons.CouponsFragmentDirections
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        observeData()
        initListeners()

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
            logSearch(newText.toString(), requireContext(), logger)
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCategoriesFilterPosition().let {
            if (it > 0) {
                binding.spinnerCategories.setSelection(it)
            }
        }
        viewModel.getShopFilterPosition().let {
            if (it > 0) {
                binding.spinnerShops.setSelection(it)
            }
        }
    }

    private fun initAdapters() {
        dealAdapter = GridDealAdapter {
            viewModel.updateDealViewsClick(it.id.toString())
            navigate(CouponsFragmentDirections.toDealFragment(it))
        }
        binding.rvDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
        binding.rvDeals.adapter = dealAdapter

        val layoutManager = binding.rvDeals.layoutManager as GridLayoutManager
        binding.rvDeals.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                if (totalItemCount <= lastVisibleItem + 50) {
                    viewModel.loadMoreDeals()
                }
            }
        })

        searchAdapter = GridDealAdapter {
            viewModel.updateDealViewsClick(it.id.toString())
            navigate(CouponsFragmentDirections.toDealFragment(it))
            binding.searchView.setQuery("", false)
        }
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
        binding.spinnerCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == viewModel.getCategoriesFilterPosition()) {
                    return
                }
                viewModel.loadCategoryDeals(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spinnerShops.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == viewModel.getShopFilterPosition()) {
                    return
                }
                viewModel.loadShopDeals(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun observeData() {
        viewModel.loadingGifVisibility.observe(viewLifecycleOwner) {
            if (it)
                binding.ivLoading.visible()
            else
                binding.ivLoading.invisible()
        }
        viewModel.deals.observe(viewLifecycleOwner, dealAdapter::submitList)
        viewModel.shops.observe(viewLifecycleOwner) {
            val adapterShops = ArrayAdapter(requireContext(), R.layout.spinner_item, getFilteredNames(it))
            adapterShops.setDropDownViewResource(R.layout.spinner_item_dropdown)
            binding.spinnerShops.adapter = adapterShops
        }
        viewModel.categories.observe(viewLifecycleOwner) {
            val adapterCategories = ArrayAdapter(requireContext(), R.layout.spinner_item, getFilteredNames(it))
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
    }

    private fun getFilteredNames(data: List<Item>): List<String> {
        val names: List<String> = data.map(Item::name)
        val mutableList = mutableListOf("All")
        mutableList.addAll(names)
        return mutableList
    }
}