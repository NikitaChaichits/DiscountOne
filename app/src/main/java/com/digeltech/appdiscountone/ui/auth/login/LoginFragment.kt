package com.digeltech.appdiscountone.ui.auth.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentLoginBinding
import com.digeltech.appdiscountone.ui.common.logLogin
import com.digeltech.appdiscountone.util.validation.isValidEmail
import com.digeltech.appdiscountone.util.validation.isValidPassword
import com.digeltech.appdiscountone.util.view.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)
    override val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        observeData()
    }

    private fun initListeners() {
        binding.toolbarLayout.ivBack.setOnClickListener {
            if (binding.webView.isVisible) {
                binding.webView.invisible()
            } else {
                navigateBack()
            }
        }
        binding.btnLogin.setOnClickListener {
            viewModel.login(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
        }
        binding.tvForgotPassword.setOnClickListener {
//            navigate(R.id.forgotPasswordFragment)
            binding.webView.openWebView("https://discount.one/wp-login.php?action=lostpassword")
        }
        binding.etEmail.doAfterTextChanged { checkLoginButtonEnable() }
        binding.etPassword.doAfterTextChanged { checkLoginButtonEnable() }
    }

    private fun checkLoginButtonEnable() {
        binding.apply {
            if (isValidEmail(etEmail.text.toString().trim()) && isValidPassword(etPassword.text.toString().trim())) {
                btnLogin.enable()
            } else {
                btnLogin.disable()
            }
        }
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                logLogin(binding.etEmail.text.toString())
                prefs.setLogin(true)
                navigate(R.id.homeFragment)
            } else {
                binding.tvPasswordError.visible()
                binding.tvForgotPassword.visible()
            }
        }
    }
}