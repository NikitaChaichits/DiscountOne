package com.digeltech.appdiscountone.ui.home

import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.ui.home.adapter.Banner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel() {

    private val _text = MutableStateFlow("This is home Fragment")
    val text: StateFlow<String> = _text.asStateFlow()

    fun getBanners(): List<Banner> {
        return listOf(
            Banner(
                id = 1,
                urlImage = "https://i.pinimg.com/originals/3d/1d/93/3d1d93618585b17e1c9210a0a0bc949a.png",
                couponId = 1
            ),
            Banner(
                id = 2,
                urlImage = "https://ledigital.ru/wp-content/uploads/2022/06/coupons-actions-3.jpg",
                couponId = 2
            ),
            Banner(
                id = 3,
                urlImage = "https://static.vecteezy.com/system/resources/previews/000/272/527/original/coupon-template-vector.jpg",
                couponId = 3
            ),
        )
    }

}