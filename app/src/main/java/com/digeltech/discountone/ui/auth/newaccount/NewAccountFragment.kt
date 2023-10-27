package com.digeltech.discountone.ui.auth.newaccount

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentNewAccountBinding
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.ui.common.logSignUp
import com.digeltech.discountone.util.log
import com.digeltech.discountone.util.time.getCurrentDateTime
import com.digeltech.discountone.util.validation.PASSWORD_MIN
import com.digeltech.discountone.util.validation.isValidEmail
import com.digeltech.discountone.util.validation.isValidPassword
import com.digeltech.discountone.util.view.disable
import com.digeltech.discountone.util.view.enable
import com.digeltech.discountone.util.view.invisible
import com.digeltech.discountone.util.view.visible
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewAccountFragment : BaseFragment(R.layout.fragment_new_account) {

    private val binding by viewBinding(FragmentNewAccountBinding::bind)
    override val viewModel: NewAccountViewModel by viewModels()

    @Inject
    lateinit var logger: AppEventsLogger

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
        viewModel.userId.observe(viewLifecycleOwner) { id ->
            logSignUp(binding.etEmail.text.toString().trim(), logger)
            prefs.setLogin(true)
            Firebase.messaging.subscribeToTopic("authorized")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed authorized"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    log(msg)
                }
            val user = User(
                id = id,
                login = binding.etName.text.toString().trim(),
                email = binding.etEmail.text.toString().trim(),
                dateRegistration = getCurrentDateTime(requireContext()),
                city = "",
                birthdate = "",
                avatarUrl = null,
                gender = null
            )
            Hawk.put(KEY_USER, user)
            navigate(R.id.onboardingFragment)
        }
        viewModel.registerError.observe(viewLifecycleOwner, ::showDialog)
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