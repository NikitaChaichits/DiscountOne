@file:Suppress("MemberVisibilityCanBePrivate")

package com.digeltech.appdiscountone.common.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.digeltech.appdiscountone.R
import com.digeltech.appdiscountone.data.source.local.SharedPreferencesDataSource
import com.digeltech.appdiscountone.util.flow.collectWhileStarted
import com.digeltech.appdiscountone.util.permission.checkPermission
import com.digeltech.appdiscountone.util.view.buildLoadingDialog
import com.digeltech.appdiscountone.util.view.dialogBuilder
import com.digeltech.appdiscountone.util.view.hideKeyboard
import com.digeltech.appdiscountone.util.view.toast

abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    abstract val viewModel: BaseViewModel

    private var loadingDialog: Dialog? = null
    lateinit var prefs: SharedPreferencesDataSource

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        prefs = SharedPreferencesDataSource(view.context)
    }

    open fun observeViewModel() {
        viewModel.loading.collectWhileStarted(viewLifecycleOwner) { showLoading(it) }
        viewModel.error.observe(viewLifecycleOwner, ::showDialogError)
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    /* Messages */
    fun toast(message: String) {
        requireContext().toast(message)
    }

    /* Dialogs */
    fun dialog(@StringRes titleStringRes: Int, @StringRes messageStringRes: Int) {
        requireContext().dialogBuilder(viewLifecycleOwner, titleStringRes)
            .message(messageStringRes)
            .show()
    }

    fun showDialogError(text: String) {
        return MaterialDialog(requireContext())
            .lifecycleOwner(viewLifecycleOwner)
            .message(text = text)
            .cornerRadius(res = R.dimen.radius_12)
            .cancelOnTouchOutside(cancelable = true)
            .show()
    }

    /* Loadings */
    fun showLoading(isLoading: Boolean) {
        loadingDialog = if (isLoading) buildLoadingDialog().apply { show() }
        else loadingDialog?.dismiss().let { null }
    }

    /* Navigation */
    fun navigate(@IdRes resId: Int) {
        findNavController().navigate(resId)
    }

    fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    fun navigate(direction: NavDirections, navOptions: NavOptions) {
        findNavController().navigate(direction, navOptions)
    }

    fun navigateBack() {
        findNavController().popBackStack()
    }

    /* Permission */
    fun checkPermission(permission: String) = requireContext().checkPermission(permission)

    /* Other */
    private fun hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }
}
