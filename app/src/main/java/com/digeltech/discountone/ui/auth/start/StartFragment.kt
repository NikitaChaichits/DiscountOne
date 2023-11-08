package com.digeltech.discountone.ui.auth.start

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentStartBinding

class StartFragment : BaseFragment(R.layout.fragment_start) {

    private val binding by viewBinding(FragmentStartBinding::bind)
    override val viewModel: StartViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        binding.btnLogin.setOnClickListener {
            navigate(R.id.loginFragment)
        }
        binding.btnCreateAccount.setOnClickListener {
            navigate(R.id.newAccountFragment)
        }
        binding.ivHome.setOnClickListener {
            navigate(R.id.homeFragment)
        }
    }
}