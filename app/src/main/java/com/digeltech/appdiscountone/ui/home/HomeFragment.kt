package com.digeltech.appdiscountone.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentHomeBinding
import com.digeltech.appdiscountone.ui.home.adapter.BannerAdapter
import com.digeltech.appdiscountone.ui.home.adapter.CategoriesAdapter
import com.digeltech.appdiscountone.util.view.setCircleImage
import com.digeltech.appdiscountone.util.view.setImageWithRadius
import com.digeltech.appdiscountone.util.view.visible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    override val viewModel: HomeViewModel by viewModels()

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileImage()
        initAdapters()
        initListeners()

        observeData()
    }

    private fun initListeners() {
        binding.ivProfile.setOnClickListener {
            navigate(R.id.profileFragment)
        }
    }

    private fun initAdapters() {
        bannerAdapter = BannerAdapter(viewModel::getDeal)
        binding.rvBanners.adapter = bannerAdapter

        categoriesAdapter = CategoriesAdapter(
            { navigate(HomeFragmentDirections.toCategoryFragment(id = it.id, title = it.name)) },
            { navigate(HomeFragmentDirections.toDealFragment(it)) }
        )
        binding.rvCategories.adapter = categoriesAdapter
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
                        setOnClickListener { viewModel.getDeal(banner.dealId) }
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
    }
}