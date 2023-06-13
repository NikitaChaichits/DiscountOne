package com.digeltech.appdiscountone.ui.auth.newaccount

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentNewAccountBinding
import com.digeltech.appdiscountone.domain.model.User
import com.digeltech.appdiscountone.ui.common.KEY_USER
import com.digeltech.appdiscountone.ui.common.logSignUp
import com.digeltech.appdiscountone.util.time.getCurrentDateTime
import com.digeltech.appdiscountone.util.validation.PASSWORD_MIN
import com.digeltech.appdiscountone.util.validation.isValidEmail
import com.digeltech.appdiscountone.util.validation.isValidPassword
import com.digeltech.appdiscountone.util.view.disable
import com.digeltech.appdiscountone.util.view.enable
import com.digeltech.appdiscountone.util.view.invisible
import com.digeltech.appdiscountone.util.view.visible
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewAccountFragment : BaseFragment(R.layout.fragment_new_account) {

    private val binding by viewBinding(FragmentNewAccountBinding::bind)
    override val viewModel: NewAccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        observeData()
    }

    private fun initListeners() {
        binding.toolbarLayout.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.btnCreateAccount.setOnClickListener {
            viewModel.register(
                binding.etName.text.toString().trim(),
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )
        }
        binding.etName.doAfterTextChanged { checkIsCreateButtonEnable() }
        binding.etEmail.doAfterTextChanged { checkIsCreateButtonEnable() }
        binding.etPassword.doAfterTextChanged {
            if (isValidPassword(it.toString().trim())) {
                checkIsCreateButtonEnable()
                binding.tvPasswordError.invisible()
                binding.tvPasswordInfo.visible()
            } else {
                if (it.toString().trim().length >= PASSWORD_MIN) {
                    binding.tvPasswordError.visible()
                    binding.tvPasswordInfo.invisible()
                    binding.tilPassword.boxStrokeColor = resources.getColor(R.color.red)
                } else {
                    binding.tvPasswordError.invisible()
                    binding.tvPasswordInfo.visible()
                    binding.tilPassword.boxStrokeColor = resources.getColor(R.color.colorPrimary)
                }
            }
        }
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                logSignUp(binding.etEmail.text.toString().trim())
                prefs.setLogin(true)
                val user = User(
                    id = "100",
                    login = binding.etName.text.toString().trim(),
                    email = binding.etEmail.text.toString().trim(),
                    dateRegistration = getCurrentDateTime(requireContext()),
                    city = "",
                    birthdate = ""
                )
                Hawk.put(KEY_USER, user)
                navigate(R.id.onboardingFragment)
            }
        }
    }

    private fun checkIsCreateButtonEnable() {
        binding.apply {
            if (!etName.text.isNullOrEmpty()
                && isValidEmail(etEmail.text.toString().trim())
                && isValidPassword(etPassword.text.toString().trim())
            ) {
                btnCreateAccount.enable()
            } else {
                btnCreateAccount.disable()
            }
        }
    }

}