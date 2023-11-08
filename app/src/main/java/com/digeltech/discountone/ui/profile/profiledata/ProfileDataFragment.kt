package com.digeltech.discountone.ui.profile.profiledata

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentProfileDataBinding
import com.digeltech.discountone.domain.model.Gender
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.util.imagepicker.ImagePicker
import com.digeltech.discountone.util.view.getImageDrawable
import com.digeltech.discountone.util.view.invisible
import com.digeltech.discountone.util.view.setCircleImage
import com.digeltech.discountone.util.view.setProfileImage
import com.google.android.material.datepicker.MaterialDatePicker
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.util.*

@AndroidEntryPoint
class ProfileDataFragment : BaseFragment(R.layout.fragment_profile_data), DatePickerDialog.OnDateSetListener {

    private val binding by viewBinding(FragmentProfileDataBinding::bind)
    override val viewModel: ProfileDataViewModel by viewModels()

    private var userPhotoUri: Uri? = null
    private var gender: Gender? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUser()
        initListeners()
        observeData()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val _month = if (month < 9) "0${month + 1}" else month + 1
        val _dayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth

        binding.tvDateOfBirth.text = "$year-$_month-$_dayOfMonth"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri = data?.data!!
                binding.ivProfileImage.setCircleImage(uri)
                userPhotoUri = uri
            }
            ImagePicker.RESULT_ERROR -> {
                toast(ImagePicker.getError(data))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.loaderProfileImage.invisible()
    }

    private fun initUser() {
        Hawk.get<User>(KEY_USER)?.let {
            binding.tvProfileEmail.text = it.email
            binding.etProfileName.setText(it.login)
            binding.tvDateOfBirth.text = it.birthdate
//            binding.etCity.setText(it.city)
            binding.tvDateRegistration.text = getString(R.string.date_of_registration, it.dateRegistration)
            it.avatarUrl?.let { url ->
                binding.ivProfileImage.setProfileImage(url)
            }
            gender = when (it.gender) {
                Gender.MALE -> {
                    binding.ivGenderMale.setImageDrawable(view?.getImageDrawable(R.drawable.ic_gender_selected))
                    Gender.MALE
                }
                Gender.FEMALE -> {
                    binding.ivGenderFemale.setImageDrawable(view?.getImageDrawable(R.drawable.ic_gender_selected))
                    Gender.FEMALE
                }
                else -> null
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
                .start(binding.loaderProfileImage)
        }
        binding.tvDateOfBirth.setOnClickListener {
//            showDatePickerDialog(requireContext(), this)
            val builder = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selection ->
                val calendar = Calendar.getInstance(TimeZone.getTimeZone(TimeZone.getDefault().id))
                calendar.timeInMillis = selection
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                val _month = if (month < 9) "0${month + 1}" else month + 1
                val _dayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth

                binding.tvDateOfBirth.text = "$year-$_month-$_dayOfMonth"
            }
            picker.show(parentFragmentManager, picker.toString())
        }
        binding.btnSave.setOnClickListener {
            if (!binding.loaderProfileImage.isVisible)
                updateProfile()
            else toast(getString(R.string.profile_photo_loading))
        }
        binding.ivGenderMale.setOnClickListener {
            binding.ivGenderMale.setImageDrawable(view?.getImageDrawable(R.drawable.ic_gender_selected))
            binding.ivGenderFemale.setImageDrawable(view?.getImageDrawable(R.drawable.ic_gender_not_selected))
            gender = Gender.MALE
        }
        binding.ivGenderFemale.setOnClickListener {
            binding.ivGenderFemale.setImageDrawable(view?.getImageDrawable(R.drawable.ic_gender_selected))
            binding.ivGenderMale.setImageDrawable(view?.getImageDrawable(R.drawable.ic_gender_not_selected))
            gender = Gender.FEMALE
        }
    }

    private fun updateProfile() {
//        val city = binding.etCity.text.toString().trim()
        val dateOfBirth = binding.tvDateOfBirth.text.toString()
        val login = binding.etProfileName.text.toString()
        var userAvatarPart: MultipartBody.Part? = null

        userPhotoUri?.let {
            val file = it.toFile()
            val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            userAvatarPart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        }

        val user = Hawk.get<User>(KEY_USER)
        if (userAvatarPart != null)
            viewModel.updateProfileWithAvatar(
                id = user.id,
                nickname = login,
                birthday = dateOfBirth,
                gender = gender,
                userAvatar = userAvatarPart
            )
        else
            viewModel.updateProfile(
                id = user.id,
                nickname = login,
                birthday = dateOfBirth,
                gender = gender,
            )
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                navigateBack()
            }
        }
    }

}