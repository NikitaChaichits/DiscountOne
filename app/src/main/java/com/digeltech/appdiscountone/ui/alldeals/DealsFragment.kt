package com.digeltech.appdiscountone.ui.alldeals

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentDealsBinding
import com.digeltech.appdiscountone.ui.common.adapter.DealAdapter
import com.digeltech.appdiscountone.ui.coupons.CouponsFragmentDirections
import com.digeltech.appdiscountone.util.view.px
import com.digeltech.appdiscountone.util.view.recycler.GridOffsetDecoration
import com.digeltech.appdiscountone.util.view.setCircleImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DealsFragment : BaseFragment(R.layout.fragment_deals) {

    private val binding by viewBinding(FragmentDealsBinding::bind)
    override val viewModel: DealsViewModel by viewModels()

    private lateinit var adapter: DealAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initListeners()

        loadProfileImage()
        observeData()
    }

    private fun initAdapter() {
        adapter = DealAdapter {
            navigate(CouponsFragmentDirections.toDealFragment(it))
        }
//        adapter.submitList(getListOfCoupons())
        binding.rvCoupons.adapter = adapter
        binding.rvCoupons.addItemDecoration(
            GridOffsetDecoration(
                edgesOffset = 0.px,
                horizontalOffset = 16.px,
                verticalOffset = 16.px
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

    private fun observeData() = Unit
}