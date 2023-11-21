package com.digeltech.discountone

import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel() {

    fun newInvitationByReferralLink(link: String) {
        viewModelScope.launch {
            repository.newUserWithReferrerLink(link)
        }
    }

}