package com.digeltech.appdiscountone.ui.profile.savedpublications

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentSavedPublicationsBinding
import com.digeltech.appdiscountone.ui.common.getListOfBookmarks
import com.digeltech.appdiscountone.util.view.px
import com.digeltech.appdiscountone.util.view.recycler.GridOffsetDecoration

class SavedPublicationsFragment : BaseFragment(R.layout.fragment_saved_publications) {

    private val binding by viewBinding(FragmentSavedPublicationsBinding::bind)
    override val viewModel: SavedPublicationsViewModel by viewModels()

    private lateinit var adapter: SavedPublicationsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initListeners()
        observeData()
    }

    private fun initAdapter() {
        adapter = SavedPublicationsAdapter { navigate(SavedPublicationsFragmentDirections.toDealFragment(it)) }

        adapter.submitList(getListOfBookmarks())
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
    }

    private fun observeData() = Unit
}