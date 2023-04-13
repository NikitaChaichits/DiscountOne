package com.digeltech.appdiscountone.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentHomeBinding
import com.digeltech.appdiscountone.ui.home.adapter.BannerAdapter
import com.digeltech.appdiscountone.ui.home.adapter.CategoriesAdapter
import com.digeltech.appdiscountone.util.view.setCircleImage
import com.digeltech.appdiscountone.util.view.setImageWithRadius
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    override val viewModel: HomeViewModel by viewModels()

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        observeData()

        loadProfileImage()
        initListeners()
        initAdapters()
        binding.ivBannerSale.setImageWithRadius("https://pbs.twimg.com/media/DWFZaMSU8AAohxh.jpg", R.dimen.radius_16)
    }

    private fun initListeners() {
        binding.ivProfile.setOnClickListener {
            navigate(R.id.profileFragment)
        }
    }

    private fun initAdapters() {
        bannerAdapter = BannerAdapter {
            navigate(HomeFragmentDirections.toDealFragment(it))
        }
        bannerAdapter.submitList(viewModel.getBanners())
        binding.rvBanners.adapter = bannerAdapter

        categoriesAdapter = CategoriesAdapter(
            { toast("Open Category $it") },
            { navigate(HomeFragmentDirections.toDealFragment(it)) }
        )
//        categoriesAdapter.submitList(getListOfCategoriesWithItems())
        binding.rvCategories.adapter = categoriesAdapter
    }

    private fun loadProfileImage() {
        Firebase.auth.currentUser?.photoUrl?.let {
            binding.ivProfile.setCircleImage(it)
        }
    }
}