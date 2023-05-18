package com.digeltech.appdiscountone.ui.coupons

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentCouponsBinding
import com.digeltech.appdiscountone.ui.common.adapter.GridDealAdapter
import com.digeltech.appdiscountone.ui.common.logSearch
import com.digeltech.appdiscountone.util.view.invisible
import com.digeltech.appdiscountone.util.view.px
import com.digeltech.appdiscountone.util.view.recycler.GridOffsetDecoration
import com.digeltech.appdiscountone.util.view.visible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CouponsFragment : BaseFragment(R.layout.fragment_coupons), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentCouponsBinding::bind)
    override val viewModel: CouponsViewModel by viewModels()

    private lateinit var dealAdapter: GridDealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        initListeners()

        observeData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNextDeals()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopLoadingDeals()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
//            viewModel.getNextDeals()
        } else {
            logSearch(newText.toString())
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    private fun initAdapters() {
        dealAdapter = GridDealAdapter {
            navigate(CouponsFragmentDirections.toDealFragment(it))
            binding.searchView.setQuery("", false)
        }
        binding.rvDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
        binding.rvDeals.adapter = dealAdapter
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
            setOnQueryTextListener(this@CouponsFragment)
            queryHint = getString(R.string.search_by_coupons)
        }
    }

    private fun observeData() {
        viewModel.deals.observe(viewLifecycleOwner, dealAdapter::submitList)
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
                binding.tvTitle.invisible()
            } else {
                binding.tvSearchResultEmpty.invisible()
                binding.tvTitle.visible()
            }
            dealAdapter.submitList(it)
        }
    }
}