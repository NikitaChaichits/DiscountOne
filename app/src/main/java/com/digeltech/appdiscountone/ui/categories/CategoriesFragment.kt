package com.digeltech.appdiscountone.ui.categories

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentCategoriesBinding
import com.digeltech.appdiscountone.ui.categories.adapter.CategoryAdapter
import com.digeltech.appdiscountone.util.view.invisible
import com.digeltech.appdiscountone.util.view.px
import com.digeltech.appdiscountone.util.view.recycler.GridOffsetDecoration
import com.digeltech.appdiscountone.util.view.setCircleImage
import com.digeltech.appdiscountone.util.view.visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CategoriesFragment : BaseFragment(R.layout.fragment_categories), SearchView.OnQueryTextListener {

    private val binding by viewBinding(FragmentCategoriesBinding::bind)

    override val viewModel: CategoriesViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initAdapters()
        initListeners()

        viewModel.getCategoriesList()

        loadProfileImage()
        observeData()
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.searchCategories(newText.toString())
        return true
    }

    private fun initAdapters() {
        categoryAdapter = CategoryAdapter {
            navigate(CategoriesFragmentDirections.toCategoryFragment(id = it.first, title = it.second))
        }
        binding.rvCategories.adapter = categoryAdapter
        binding.rvCategories.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
            )
        )
    }

    private fun initListeners() {
        binding.ivProfile.setOnClickListener {
            if (auth.currentUser == null) {
                navigate(R.id.startFragment)
            } else {
                navigate(R.id.profileFragment)
            }
        }
        binding.searchView.setOnQueryTextListener(this)
        binding.searchView.queryHint = getString(R.string.search_by_categories)
    }

    private fun loadProfileImage() {
        Firebase.auth.currentUser?.photoUrl?.let(binding.ivProfile::setCircleImage)
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect(categoryAdapter::submitList)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult.collect {
                if (it.isEmpty() && !binding.searchView.query.isNullOrEmpty()) {
                    binding.tvSearchResultEmpty.visible()
                    binding.tvTitle.invisible()
                } else {
                    binding.tvSearchResultEmpty.invisible()
                    binding.tvTitle.visible()
                }
                categoryAdapter.submitList(it)
            }
        }
    }
}