package com.digeltech.discountone.ui.auth.forgot

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentForgotPasswordBinding
import com.digeltech.discountone.util.validation.isValidEmail
import com.digeltech.discountone.util.view.disable
import com.digeltech.discountone.util.view.enable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment(R.layout.fragment_forgot_password) {

    private val binding by viewBinding(FragmentForgotPasswordBinding::bind)
    override val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        observeData()
    }

    private fun initListeners() {
        binding.toolbarLayout.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.etEmail.doAfterTextChanged {
            if (isValidEmail(binding.etEmail.text.toString().trim())) {
                binding.btnSendReset.enable()
            } else {
                binding.btnSendReset.disable()
            }
        }
        binding.btnSendReset.setOnClickListener {
            viewModel.resetPassword(binding.etEmail.text.toString().trim())
        }
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) {
            if (it)
                showDialog(getString(R.string.recovery_pass_title))
            else
                showDialog("${binding.etEmail.text.toString().trim()} user does not exist")
        }
    }
}