package com.digeltech.discountone.ui.auth.recovery

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentRecoveryPasswordBinding
import com.digeltech.discountone.util.validation.PASSWORD_MIN
import com.digeltech.discountone.util.validation.isValidPassword
import com.digeltech.discountone.util.view.*
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoveryPasswordFragment : BaseFragment(R.layout.fragment_recovery_password) {

    override val viewModel: RecoveryPasswordViewModel by viewModels()

    private val binding by viewBinding(FragmentRecoveryPasswordBinding::bind)
    private val args: RecoveryPasswordFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        observeData()
    }

    private fun initListeners() {
        binding.toolbarLayout.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.etPassword.doAfterTextChanged {
            checkPassword(it, binding.tilPassword)
        }
        binding.etPasswordRepeat.doAfterTextChanged {
            checkPassword(it, binding.tilPasswordRepeat)
        }
        binding.btnReset.setOnClickListener {
            viewModel.resetPassword(userId = args.userId, password = binding.etPassword.text.toString().trim())
        }
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                navigate(R.id.loginFragment)
            }
        }
    }

    private fun checkPassword(it: Editable?, til: TextInputLayout) {
        if (isValidPassword(it.toString().trim())) {
            checkIsResetButtonEnable()
            til.boxStrokeColor = resources.getColor(R.color.colorPrimary)
            binding.tvPasswordError.gone()
            binding.tvPasswordInfo.gone()
        } else {
            if (it.toString().trim().length >= PASSWORD_MIN) {
                binding.tvPasswordError.visible()
                binding.tvPasswordInfo.invisible()
                til.boxStrokeColor = resources.getColor(R.color.red)
            } else {
                binding.tvPasswordError.invisible()
                binding.tvPasswordInfo.visible()
                til.boxStrokeColor = resources.getColor(R.color.colorPrimary)
            }
        }
    }

    private fun checkIsResetButtonEnable() {
        binding.apply {
            if (etPassword.text.toString().trim() == etPasswordRepeat.text.toString().trim()) {
                btnReset.enable()
            } else {
                binding.tvPasswordInfo.text = getString(R.string.error_password_repeat)
                binding.tvPasswordInfo.visible()
                btnReset.disable()
            }
        }
    }
}