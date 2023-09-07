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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentOnboardingBinding
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.util.log
import com.digeltech.discountone.util.view.disable
import com.digeltech.discountone.util.view.enable
import com.digeltech.discountone.util.view.setCircleImage
import com.digeltech.discountone.util.view.showDatePickerDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody

@AndroidEntryPoint
class OnboardingFragment : BaseFragment(R.layout.fragment_onboarding),
    DatePickerDialog.OnDateSetListener {

    private val binding by viewBinding(FragmentOnboardingBinding::bind)
    override val viewModel: OnboardingViewModel by viewModels()

    private var userPhotoUri: Uri? = null

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
            }
            ImagePicker.RESULT_ERROR -> {
                toast(ImagePicker.getError(data))
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

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) {
            if (it) {
                val user = Hawk.get<User>(KEY_USER)
                Hawk.put(
                    KEY_USER,
                    user.copy(
                        city = binding.etCity.text.toString().trim(),
                        birthdate = binding.tvDateOfBirth.text.toString(),
                    )
                )
                log("OnboardingFragment put $user")
                navigateBack()
            }
        }
    }

    private fun updateProfile() {
        val city = binding.etCity.text.toString().trim()
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
                city = city,
                birthday = dateOfBirth,
                userAvatar = userAvatarPart
            )
        else
            viewModel.updateProfile(
                id = user.id,
                city = city,
                birthday = dateOfBirth,
            )
    }

    private fun checkIsContinueButtonEnable() {
        if (binding.tvDateOfBirth.text.isNotEmpty() && binding.etCity.text.toString().trim().isNotEmpty()) {
            binding.btnContinue.enable()
        } else {
            binding.btnContinue.disable()
        }
    }
}