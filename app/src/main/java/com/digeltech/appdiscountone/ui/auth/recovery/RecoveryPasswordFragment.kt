package com.digeltech.appdiscountone.ui.auth.recovery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.databinding.FragmentRecoveryPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoveryPasswordFragment : BaseFragment(R.layout.fragment_recovery_password) {

    private val binding by viewBinding(FragmentRecoveryPasswordBinding::bind)
    override val viewModel: RecoveryPasswordViewModel by viewModels()
    private val args: RecoveryPasswordFragmentArgs by navArgs()

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
        binding.btnGoToLogin.setOnClickListener {
            navigate(R.id.loginFragment)
        }
        binding.btnResendEmail.setOnClickListener {
            Firebase.auth.sendPasswordResetEmail(args.email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("We've sent you an email with a link to reset your password")
                    }
                }
        }
    }
}