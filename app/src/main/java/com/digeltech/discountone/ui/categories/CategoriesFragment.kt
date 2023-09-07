package com.digeltech.discountone.ui.categories

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentCategoriesBinding
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.categories.adapter.CategoryAdapter
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.ui.common.adapter.GridDealAdapter
import com.digeltech.discountone.ui.common.logOpenCategoryDeals
import com.digeltech.discountone.ui.common.logSearch
import com.digeltech.discountone.ui.home.HomeFragmentDirections
import com.digeltech.discountone.util.view.*
import com.digeltech.discountone.util.view.recycler.GridOffsetDecoration
import com.facebook.appevents.AppEventsLogger
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CategoriesFragment : BaseFragment(R.layout.fragment_categories), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentCategoriesBinding::bind)

    override val viewModel: CategoriesViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var searchDealAdapter: GridDealAdapter

    @Inject
    lateinit var logger: AppEventsLogger

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        initListeners()

        viewModel.getCategoriesList()

        loadProfileImage()
        observeData()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText.isNullOrEmpty()) {
            binding.rvSearchDeals.invisible()
            binding.rvCategories.visible()
            binding.tvSearchResultEmpty.invisible()
            binding.tvTitle.visible()
        } else {
            logSearch(newText.toString(), requireContext(), logger)
            viewModel.searchDeals(newText.toString())
        }
        return true
    }

    private fun initAdapters() {
        categoryAdapter = CategoryAdapter {
            navigate(
                CategoriesFragmentDirections.toCategoryFragment(
                    id = it.id,
                    title = it.name,
                    slug = it.slug
                )
            )
            logOpenCategoryDeals(it.name, requireContext(), logger)
        }
        binding.rvCategories.adapter = categoryAdapter
        binding.rvCategories.addItemDecoration(
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
            setOnQueryTextListener(this@CategoriesFragment)
            queryHint = getString(R.string.search_by_deals)
        }
    }

    private fun loadProfileImage() {
        Hawk.get<User>(KEY_USER)?.let {
            it.avatarUrl?.let { url ->
                binding.ivProfile.setProfileImage(url)
            }
        }
    }

    private fun observeData() {
        viewModel.categories.observe(viewLifecycleOwner, categoryAdapter::submitList)
        viewModel.searchResult.observe(viewLifecycleOwner) {
            if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                binding.tvSearchResultEmpty.visible()
            } else {
                binding.tvSearchResultEmpty.invisible()
            }
            searchDealAdapter.submitList(it)
            binding.rvSearchDeals.visible()
            binding.tvTitle.invisible()
            binding.rvCategories.invisible()
        }
    }
}