package com.digeltech.appdiscountone.ui.auth.newaccount

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.appdiscountone.databinding.FragmentNewAccountBinding
import com.digeltech.appdiscountone.util.log
import com.digeltech.appdiscountone.util.time.getCurrentDateTime
import com.digeltech.appdiscountone.util.validation.PASSWORD_MIN
import com.digeltech.appdiscountone.util.validation.isValidEmail
import com.digeltech.appdiscountone.util.validation.isValidPassword
import com.digeltech.appdiscountone.util.view.disable
import com.digeltech.appdiscountone.util.view.enable
import com.digeltech.appdiscountone.util.view.invisible
import com.digeltech.appdiscountone.util.view.visible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class NewAccountFragment : BaseFragment(R.layout.fragment_new_account) {

    private val binding by viewBinding(FragmentNewAccountBinding::bind)
    override val viewModel: NewAccountViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    lateinit var prefs: SharedPreferencesDataSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        prefs = SharedPreferencesDataSource(view.context)

        initListeners()
    }

    private fun initListeners() {
        binding.toolbarLayout.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.btnCreateAccount.setOnClickListener {
            createAccount(binding.etEmail.text.toString(), binding.etPassword.text.toString())
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

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    updateProfile()
                    Firebase.auth.currentUser?.sendEmailVerification()
                    navigate(R.id.onboardingFragment)
                } else {
                    log("createUserWithEmail:failure ${task.exception}")
                    toast("Authentication failed")
                }
            }
    }

    private fun updateProfile() {
        prefs.setName(binding.etName.text.toString().trim())
        prefs.setDateOfRegistration(getCurrentDateTime(requireContext()))
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