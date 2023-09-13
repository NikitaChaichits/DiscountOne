package com.digeltech.discountone.ui.profile.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import by.kirich1409.viewbindingdelegate.viewBinding
import com.digeltech.discountone.R
import com.digeltech.discountone.common.base.BaseFragment
import com.digeltech.discountone.databinding.FragmentProfileBinding
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.ui.common.KEY_USER
import com.digeltech.discountone.util.view.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.orhanobut.hawk.Hawk


class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val binding by viewBinding(FragmentProfileBinding::bind)
    override val viewModel: ProfileViewModel by viewModels()

    private lateinit var bottomNavMenu: BottomNavigationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavMenu = requireActivity().findViewById(R.id.bottomNavMenu)

        initListeners()
    }

    override fun onResume() {
        super.onResume()
        initUser()
    }

    private fun initUser() {
        Hawk.get<User>(KEY_USER)?.let { user ->
            binding.tvProfileName.text = user.login
            binding.tvProfileEmail.text = user.email
            user.avatarUrl?.let { url ->
                binding.ivProfileImage.setProfileImage(url)
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
            llNotification.setOnClickListener {
                navigate(R.id.notificationsFragment)
            }
            llLogout.setOnClickListener {
                Firebase.auth.signOut()
                prefs.clear()
                Hawk.deleteAll()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.startFragment, false)
                    .build()
                navigate(ProfileFragmentDirections.toStartFragment(), navOptions)
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