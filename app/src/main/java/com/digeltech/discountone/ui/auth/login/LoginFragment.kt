package com.digeltech.discountone.ui.auth.login

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentLoginBinding
import com.digeltech.discountone.ui.common.logLogin
import com.digeltech.discountone.util.log
import com.digeltech.discountone.util.validation.isValidEmail
import com.digeltech.discountone.util.validation.isValidPassword
import com.digeltech.discountone.util.view.*
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)
    override val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var logger: AppEventsLogger

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        observeData()
    }

    private fun initListeners() {
        binding.toolbarLayout.ivBack.setOnClickListener {
            if (binding.webView.isVisible) {
                binding.grContent.visible()
                binding.webView.invisible()
            } else {
                navigateBack()
            }
        }
        binding.btnLogin.setOnClickListener {
            viewModel.login(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
        }
        binding.btnCreateAccount.setOnClickListener {
            navigate(R.id.newAccountFragment)
        }
        binding.tvForgotPassword.setOnClickListener {
            binding.grContent.invisible()
            binding.tvPasswordError.gone()
            binding.tvForgotPassword.gone()
            binding.webView.openWebView("https://discount.one/wp-login.php?action=lostpassword", binding.ivLoading)
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
            logLogin(binding.etEmail.text.toString(), logger)
            prefs.setLogin(true)
            Firebase.messaging.subscribeToTopic("authorized")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed authorized"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    log(msg)
                }
            navigate(R.id.homeFragment)
        }
        viewModel.loginError.observe(viewLifecycleOwner) {
            binding.tvPasswordError.visible()
            binding.tvForgotPassword.visible()
        }
    }
}