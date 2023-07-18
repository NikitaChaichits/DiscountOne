package com.digeltech.discountone.ui.profile.profiledata

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentProfileDataBinding
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.util.view.setCircleImage
import com.digeltech.discountone.util.view.showDatePickerDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDataFragment : BaseFragment(R.layout.fragment_profile_data), DatePickerDialog.OnDateSetListener {

    private val binding by viewBinding(FragmentProfileDataBinding::bind)
    override val viewModel: ProfileDataViewModel by viewModels()

    private var userPhotoUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUser()
        initListeners()
        observeData()
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
        Hawk.get<User>(KEY_USER)?.let {
            binding.tvProfileEmail.text = it.email
            binding.etProfileName.setText(it.login)
            binding.tvDateOfBirth.text = it.birthdate
            binding.etCity.setText(it.city)
            binding.tvDateRegistration.text = getString(R.string.date_of_registration, it.dateRegistration)
        }

        Firebase.auth.currentUser?.let {
            it.photoUrl?.let { uri ->
                binding.ivProfileImage.setCircleImage(uri)
            }
        }
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
        val city = binding.etCity.text.toString().trim()
        val dateOfBirth = binding.tvDateOfBirth.text.toString()
        val login = binding.etProfileName.text.toString()
        val user = Hawk.get<User>(KEY_USER)
        viewModel.updateProfile(id = user.id, city = city, birthday = dateOfBirth, login = login)
        updateFirebaseProfile()
    }

    private fun updateFirebaseProfile() {
        val profileUpdates = userProfileChangeRequest {
            photoUri = userPhotoUri
        }
        Firebase.auth.currentUser?.updateProfile(profileUpdates)
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                val user = Hawk.get<User>(KEY_USER)
                Hawk.put(
                    KEY_USER, user.copy(
                        city = binding.etCity.text.toString().trim(),
                        birthdate = binding.tvDateOfBirth.text.toString(),
                        login = binding.etProfileName.text.toString()
                    )
                )
                navigateBack()
            }
        }
    }

}