package com.digeltech.appdiscountone.ui.auth.onboarding

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.appdiscountone.databinding.FragmentOnboardingBinding
import com.digeltech.appdiscountone.util.view.disable
import com.digeltech.appdiscountone.util.view.enable
import com.digeltech.appdiscountone.util.view.setCircleImage
import com.digeltech.appdiscountone.util.view.showDatePickerDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class OnboardingFragment : BaseFragment(R.layout.fragment_onboarding),
    DatePickerDialog.OnDateSetListener {

    private val binding by viewBinding(FragmentOnboardingBinding::bind)
    override val viewModel: OnboardingViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var prefs: SharedPreferencesDataSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        prefs = SharedPreferencesDataSource(view.context)
        initListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = data?.data!!
                binding.ivProfileImage.setCircleImage(uri)
                updateFirebaseProfile(uri)
            }
            ImagePicker.RESULT_ERROR -> {
                toast(ImagePicker.getError(data))
            }
        }
    }

    private fun initListeners() {
        binding.ivProfileImage.setOnClickListener {
            ImagePicker.with(this)
                .compress(512)
                .cropSquare()
                .start()
        }
        binding.tvDateOfBirth.setOnClickListener {
            showDatePickerDialog(requireContext(), this)
        }
        binding.etCity.doAfterTextChanged {
            checkIsContinueButtonEnable()
        }
        binding.btnContinue.setOnClickListener {
            updateProfile()
            navigate(R.id.homeFragment)
        }
        binding.btnSkip.setOnClickListener {
            navigate(R.id.homeFragment)
        }
    }

    private fun updateProfile() {
        val city = binding.etCity.text.toString().trim()
        val dateOfBirth = binding.tvDateOfBirth.text.toString()
        if (city.isNotEmpty()) {
            prefs.setCity(city)
        }
        if (dateOfBirth.isNotEmpty()) {
            prefs.setDateOfBirth(dateOfBirth)
        }
    }

    private fun updateFirebaseProfile(uri: Uri) {
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
        }
        Firebase.auth.currentUser?.updateProfile(profileUpdates)
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        binding.tvDateOfBirth.text = "$year-$month-$dayOfMonth"
        checkIsContinueButtonEnable()
    }

    private fun checkIsContinueButtonEnable() {
        if (binding.tvDateOfBirth.text.isNotEmpty() && binding.etCity.text.toString().trim().isNotEmpty()) {
            binding.btnContinue.enable()
        } else {
            binding.btnContinue.disable()
        }
    }
}