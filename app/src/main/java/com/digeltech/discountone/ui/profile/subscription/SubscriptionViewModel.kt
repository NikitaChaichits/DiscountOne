package com.digeltech.discountone.ui.profile.subscription

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digeltech.discountone.common.base.BaseViewModel
import com.digeltech.discountone.domain.model.SubscriptionCategory
import com.digeltech.discountone.domain.model.User
import com.digeltech.discountone.domain.repository.CategoriesRepository
import com.digeltech.discountone.ui.common.KEY_USER
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
) : BaseViewModel() {

    private val _categories: MutableLiveData<List<SubscriptionCategory>> = MutableLiveData()
    val categories: LiveData<List<SubscriptionCategory>> = _categories

    private val _isEmailNotificationOn: MutableLiveData<Boolean> = MutableLiveData()
    val isEmailNotificationOn: LiveData<Boolean> = _isEmailNotificationOn

    private var categoriesForUnsubscribe = mutableSetOf<Int>()

    init {
        initSubscriptionCategories()
    }

    private fun initSubscriptionCategories() {
        Hawk.get<User>(KEY_USER)?.let {
            viewModelScope.launchWithLoading {
                categoriesRepository.getSubscriptionCategories(it.id)
                    .onSuccess {
                        _categories.postValue(it.subscriptionCategories)
                        _isEmailNotificationOn.postValue(it.emailNotification)
                        it.subscriptionCategories.forEach { category ->
                            if (category.isNotificationOff) {
                                categoriesForUnsubscribe.add(category.id)
                            }
                        }
                    }
                    .onFailure { error.postValue(it.toString()) }
            }
        }
    }

    fun updateSubscriptionCategories(
        isEmailNotificationOn: Boolean?,
    ) {
        Hawk.get<User>(KEY_USER)?.let {
            val unselectedNotificationCategories = categoriesForUnsubscribe.joinToString(",")
            viewModelScope.launchWithLoading {
                categoriesRepository.updateSubscriptionCategories(
                    userId = it.id,
                    isEmailNotificationOn = isEmailNotificationOn,
                    unselectedNotificationCategories = unselectedNotificationCategories
                )
                    .onSuccess { success.postValue(true) }
                    .onFailure { error.postValue(it.toString()) }
            }
        }
    }

    fun checkCategoriesForUnsubscribe(id: Int) {
        if (categoriesForUnsubscribe.contains(id))
            categoriesForUnsubscribe.remove(id)
        else categoriesForUnsubscribe.add(id)
    }
}