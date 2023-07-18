package com.digeltech.discountone.ui.auth.forgot

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentForgotPasswordBinding
import com.digeltech.discountone.util.log
import com.digeltech.discountone.util.validation.isValidEmail
import com.digeltech.discountone.util.view.disable
import com.digeltech.discountone.util.view.enable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordFragment : BaseFragment(R.layout.fragment_forgot_password) {

    private val binding by viewBinding(FragmentForgotPasswordBinding::bind)
    override val viewModel: ForgotPasswordViewModel by viewModels()

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
        binding.etEmail.doAfterTextChanged {
            if (isValidEmail(binding.etEmail.text.toString().trim())) {
                binding.btnSendReset.enable()
            } else {
                binding.btnSendReset.disable()
            }
        }
        binding.btnSendReset.setOnClickListener {
            sendPasswordReset(binding.etEmail.text.toString().trim())
        }
    }

    private fun sendPasswordReset(email: String) {
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val action = ForgotPasswordFragmentDirections.toRecoveryPasswordFragment(email)
                    navigate(action)
                } else {
                    toast("Failed to recovery password. Try again later")
                    log("recovery password:failure ${task.exception}")
                }
            }
    }
}