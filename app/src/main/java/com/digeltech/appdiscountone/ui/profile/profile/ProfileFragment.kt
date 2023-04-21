package com.digeltech.appdiscountone.ui.profile.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.common.base.BaseFragment
import com.digeltech.appdiscountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.appdiscountone.databinding.FragmentProfileBinding
import com.digeltech.appdiscountone.util.view.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk


class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    override val viewModel: ProfileViewModel by viewModels()

    private lateinit var prefs: SharedPreferencesDataSource
    private lateinit var bottomNavMenu: BottomNavigationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPreferencesDataSource(view.context)
        bottomNavMenu = requireActivity().findViewById(R.id.bottomNavMenu)

        initListeners()
    }

    override fun onResume() {
        super.onResume()
        initUser()
    }


    private fun initUser() {
        Firebase.auth.currentUser?.let {
            binding.tvProfileName.text = it.displayName
            binding.tvProfileEmail.text = it.email
            it.photoUrl?.let { uri ->
                binding.ivProfileImage.setCircleImage(uri)
            }
        }
    }

    private fun initListeners() {
        binding.apply {
            ivBack.setOnClickListener {
                if (webView.isVisible) {
                    webView.invisible()
                    binding.tvTitle.text = getString(R.string.profile)
                    bottomNavMenu.visible()
                } else {
                    navigateBack()
                }
            }
            llPersonalData.setOnClickListener {
                navigate(R.id.profileDataFragment)
            }
            llSavedPublication.setOnClickListener {
                navigate(R.id.savedPublicationsFragment)
            }
            llLogout.setOnClickListener {
                Firebase.auth.signOut()
                prefs.clear()
                Hawk.deleteAll()
                navigate(R.id.startFragment)
            }
            llPrivacyPolicy.setOnClickListener {
                binding.webView.openWebView(getString(R.string.privacy_policy_link))
                binding.tvTitle.text = getString(R.string.privacy_policy)
                bottomNavMenu.gone()
            }
            llConditionsOfUse.setOnClickListener {
                binding.webView.openWebView(getString(R.string.terms_of_use_link))
                binding.tvTitle.text = getString(R.string.conditions_of_use)
                bottomNavMenu.gone()
            }
            llAffiliateDisclosure.setOnClickListener {
                binding.webView.openWebView(getString(R.string.affiliate_disclosure_link))
                binding.tvTitle.text = getString(R.string.affiliate_disclosure)
                bottomNavMenu.gone()
            }
        }
    }
}