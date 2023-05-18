package com.digeltech.appdiscountone.ui.auth.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentLoginBinding
import com.digeltech.appdiscountone.ui.common.logLogin
import com.digeltech.appdiscountone.util.validation.isValidEmail
import com.digeltech.appdiscountone.util.validation.isValidPassword
import com.digeltech.appdiscountone.util.view.disable
import com.digeltech.appdiscountone.util.view.enable
import com.digeltech.appdiscountone.util.view.visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : BaseFragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)
    override val viewModel: LoginViewModel by viewModels()

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        initListeners()
    }

    private fun initListeners() {
        binding.toolbarLayout.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.btnLogin.setOnClickListener {
            auth.signInWithEmailAndPassword(
                binding.etEmail.text.toString().trim(),
                binding.etPassword.text.toString().trim()
            )
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        logLogin(binding.etEmail.text.toString().trim())
                        navigate(R.id.homeFragment)
                    } else {
                        binding.tvPasswordError.visible()
                        binding.tvForgotPassword.visible()
                    }
                }
        }
        binding.tvForgotPassword.setOnClickListener {
            navigate(R.id.forgotPasswordFragment)
        }
        binding.etEmail.doAfterTextChanged { checkLoginButtonEnable() }
        binding.etPassword.doAfterTextChanged { checkLoginButtonEnable() }
    }

    private fun checkLoginButtonEnable() {
        binding.apply {
            if (isValidEmail(etEmail.text.toString().trim())
                && isValidPassword(etPassword.text.toString().trim())
            ) {
                btnLogin.enable()
            } else {
                btnLogin.disable()
            }
        }
    }
}