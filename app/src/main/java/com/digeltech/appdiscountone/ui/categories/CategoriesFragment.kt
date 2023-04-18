package com.digeltech.appdiscountone.ui.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentCategoriesBinding
import com.digeltech.appdiscountone.ui.categories.adapter.CategoryAdapter
import com.digeltech.appdiscountone.util.view.recycler.GridOffsetDecoration
import com.digeltech.appdiscountone.util.view.setCircleImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoriesFragment : BaseFragment(R.layout.fragment_categories) {

    private val binding by viewBinding(FragmentCategoriesBinding::bind)

    override val viewModel: CategoriesViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        initListeners()

        viewModel.getCategoriesList()

        loadProfileImage()
        observeData()
    }

    private fun initAdapters() {
        categoryAdapter = CategoryAdapter {
            navigate(CategoriesFragmentDirections.toCategoryFragment(categoryId = it.first, categoryName = it.second))
        }
        binding.rvCategories.adapter = categoryAdapter
        binding.rvCategories.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 16,
                horizontalOffset = 16,
                verticalOffset = 16
            )
        )
    }

    private fun initListeners() {
        binding.ivProfile.setOnClickListener {
            navigate(R.id.profileFragment)
        }
    }

    private fun loadProfileImage() {
        Firebase.auth.currentUser?.photoUrl?.let {
            binding.ivProfile.setCircleImage(it)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect(categoryAdapter::submitList)
        }
    }
}