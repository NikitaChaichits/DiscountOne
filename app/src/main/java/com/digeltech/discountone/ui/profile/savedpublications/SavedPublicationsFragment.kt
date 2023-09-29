package com.digeltech.discountone.ui.profile.savedpublications

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentSavedPublicationsBinding
import com.digeltech.discountone.ui.common.logSearch
import com.digeltech.discountone.util.view.invisible
import com.digeltech.discountone.util.view.px
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.digeltech.discountone.util.view.visible
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SavedPublicationsFragment : BaseFragment(R.layout.fragment_saved_publications), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentSavedPublicationsBinding::bind)
    override val viewModel: SavedPublicationsViewModel by viewModels()

    @Inject
    lateinit var logger: AppEventsLogger

    private lateinit var adapter: SavedPublicationsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initListeners()
        observeData()

        viewModel.getSavedPublications()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            viewModel.getSavedPublications()
        } else {
            logSearch(newText.toString(), logger)
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    private fun initAdapter() {
        adapter = SavedPublicationsAdapter(
            {
                navigate(SavedPublicationsFragmentDirections.toDealFragment(it))
            },
            logger
        )
        binding.rvDeals.adapter = adapter
        binding.rvDeals.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.searchView.apply {
            setOnClickListener { onActionViewExpanded() }
            setOnQueryTextListener(this@SavedPublicationsFragment)
            queryHint = getString(R.string.search_by_deals)
        }
    }

    private fun observeData() {
        viewModel.loadingError.observe(viewLifecycleOwner) {
            if (it)
                binding.tvLoadingResultEmpty.visible()
        }
        viewModel.deals.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.tvSearchResultEmpty.invisible()
            binding.tvLoadingResultEmpty.invisible()
            binding.tvTitle.visible()
        }
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
                binding.tvTitle.invisible()
            } else {
                binding.tvSearchResultEmpty.invisible()
                binding.tvTitle.visible()
            }
            adapter.submitList(it)
        }
    }
}
