package com.digeltech.appdiscountone.ui.shops

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentShopsBinding
import com.digeltech.appdiscountone.ui.categories.CategoriesFragmentDirections
import com.digeltech.appdiscountone.ui.shops.adapter.ShopAdapter
import com.digeltech.appdiscountone.util.view.setCircleImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopsFragment : BaseFragment(R.layout.fragment_shops) {

    private val binding by viewBinding(FragmentShopsBinding::bind)

    override val viewModel: ShopsViewModel by viewModels()

    private lateinit var shopAdapter: ShopAdapter
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initAdapter()
        initListeners()

        viewModel.getShopsList()

        loadProfileImage()
        observeData()
    }

    private fun initAdapter() {
        shopAdapter = ShopAdapter {
            navigate(CategoriesFragmentDirections.toCategoryFragment(id = it.first, title = it.second))
        }
        binding.rvShops.adapter = shopAdapter
    }

    private fun initListeners() {
        binding.ivProfile.setOnClickListener {
            if (auth.currentUser == null) {
                navigate(R.id.startFragment)
            } else {
                navigate(R.id.profileFragment)
            }
        }
    }

    private fun loadProfileImage() {
        Firebase.auth.currentUser?.photoUrl?.let {
            binding.ivProfile.setCircleImage(it)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.shops.collect(shopAdapter::submitList)
        }
    }
}