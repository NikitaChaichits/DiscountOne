package com.digeltech.appdiscountone.ui.profile.profiledata

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.appdiscountone.databinding.FragmentProfileDataBinding
import com.digeltech.appdiscountone.util.view.setCircleImage
import com.digeltech.appdiscountone.util.view.showDatePickerDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class ProfileDataFragment : BaseFragment(R.layout.fragment_profile_data), DatePickerDialog.OnDateSetListener {

    private val binding by viewBinding(FragmentProfileDataBinding::bind)
    override val viewModel: ProfileDataViewModel by viewModels()

    private lateinit var prefs: SharedPreferencesDataSource
    private var userPhotoUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPreferencesDataSource(view.context)

        initUser()
        initListeners()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        binding.tvDateOfBirth.text = "$year-$month-$dayOfMonth"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val uri: Uri = data?.data!!
                binding.ivProfileImage.setCircleImage(uri)
                userPhotoUri = uri
            }
            ImagePicker.RESULT_ERROR -> {
                toast(ImagePicker.getError(data))
            }
        }
    }

    private fun initUser() {
        Firebase.auth.currentUser?.let {
            it.photoUrl?.let { uri ->
                binding.ivProfileImage.setCircleImage(uri)
            }
            binding.tvProfileEmail.text = it.email
            binding.etProfileName.setText(it.displayName)
        }

        val dateOfBirth = prefs.getDateOfBirth()
        if (dateOfBirth.isNotEmpty())
            binding.tvDateOfBirth.text = dateOfBirth

        val city = prefs.getCity()
        if (city.isNotEmpty())
            binding.etCity.setText(city)

        binding.tvDateRegistration.text = getString(R.string.date_of_registration, prefs.getDateOfRegistration())
    }

    private fun initListeners() {
        binding.ivBack.setOnClickListener {
            navigateBack()
        }
        binding.ivProfileImage.setOnClickListener {
            ImagePicker.with(this)
                .compress(512)
                .cropSquare()
                .start()
        }
        binding.tvDateOfBirth.setOnClickListener {
            showDatePickerDialog(requireContext(), this)
        }
        binding.btnSave.setOnClickListener {
            updateProfile()
        }
    }

    private fun updateProfile() {
        prefs.setDateOfBirth(binding.tvDateOfBirth.text.toString())
        prefs.setCity(binding.etCity.text.toString().trim())
        updateFirebaseProfile()
        navigateBack()
    }

    private fun updateFirebaseProfile() {
        val profileUpdates = userProfileChangeRequest {
            displayName = binding.etProfileName.text.toString()
            photoUri = userPhotoUri
        }
        Firebase.auth.currentUser?.updateProfile(profileUpdates)
    }

}