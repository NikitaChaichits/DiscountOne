package com.digeltech.discountone.ui.auth.onboarding

import android.annotation.SuppressLint
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
import com.digeltech.discountone.databinding.FragmentOnboardingBinding
import com.digeltech.discountone.domain.model.Gender
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.util.imagepicker.ImagePicker
import com.digeltech.discountone.util.view.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.util.*

@AndroidEntryPoint
class OnboardingFragment : BaseFragment(R.layout.fragment_onboarding), DatePickerDialog.OnDateSetListener {

    private val binding by viewBinding(FragmentOnboardingBinding::bind)
    override val viewModel: OnboardingViewModel by viewModels()

    private var userPhotoUri: Uri? = null
    private var gender: Gender? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        observeData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri = data?.data!!
                binding.ivProfileImage.setCircleImage(uri)
                userPhotoUri = uri
                binding.loaderProfileImage.invisible()
            }
            ImagePicker.RESULT_ERROR -> {
                toast(ImagePicker.getError(data))
                binding.loaderProfileImage.invisible()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        binding.tvDateOfBirth.text = "$year-$month-$dayOfMonth"
        checkIsContinueButtonEnable()
    }

    private fun initListeners() {
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
//        binding.etCity.doAfterTextChanged {
//            checkIsContinueButtonEnable()
//        }
        binding.btnContinue.setOnClickListener {
            if (!binding.loaderProfileImage.isVisible)
                updateProfile()
            else toast(getString(R.string.profile_photo_loading))
        }
        binding.btnSkip.setOnClickListener {
            navigate(R.id.homeFragment)
        }
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                navigate(R.id.profileFragment)
            }
        }
    }

    private fun updateProfile() {
//        val city = binding.etCity.text.toString().trim()
        val dateOfBirth = binding.tvDateOfBirth.text.toString()
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
                birthday = dateOfBirth,
                gender = gender,
                userAvatar = userAvatarPart
            )
        else
            viewModel.updateProfile(
                id = user.id,
                gender = gender,
                birthday = dateOfBirth,
            )
    }

    private fun checkIsContinueButtonEnable() {
        if (binding.tvDateOfBirth.text.isNotEmpty()) {
            binding.btnContinue.enable()
        } else {
            binding.btnContinue.disable()
        }
    }
}