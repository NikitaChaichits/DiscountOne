package com.digeltech.appdiscountone.ui.deal

import com.digeltech.appdiscountone.common.base.BaseViewModel
import com.digeltech.appdiscountone.domain.model.Deal
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DealViewModel @Inject constructor() : BaseViewModel() {

    fun getDealData(): Deal {
        return Deal(
            id = 1,
            description = "Industry Leading noise cancellation-two processors control 8 microphones for unprecedented noise cancellation. With Auto NC Optimizer, noise canceling is automatically optimized based on your wearing conditions and environment.\n" +
                    "Magnificent Sound, engineered to perfection with the new Integrated Processor V1\n" +
                    "Crystal clear hands-free calling with 4 beamforming microphones, precise voice pickup, and advanced audio signal processing.\n" +
                    "Up to 30-hour battery life with quick charging (3 min charge for 3 hours of playback).Note:If you face issue in Bluetooth connectivity please turn off the Bluetooth function for a couple of minutes, then turn it back on\n" +
                    "Ultra-comfortable, lightweight design with soft fit leather\n" +
                    "Multipoint connection allows you to quickly switch between devices\n" +
                    "Carry your headphones effortlessly in the redesigned case.\n" +
                    "Intuitive touch control settings to pause play skip tracks, control volume, activate your voice assistant, and answer phone calls.\n" +
                    "For everyday convenience, just Speak-to-Chat and Quick Attention mode stop your music and let in ambient sound\n" +
                    "With instant pause/instant play music automatically pauses when headphones are taken off and starts again when they are put back on",
            title = "WH-1000XM5 Wireless Industry Leading Noise Canceling Headphones",
            imageUrl = "https://discount.one/wp-content/uploads/2023/04/frame_780-82.webp",
            companyName = "Nike",
            companyLogoUrl = null,
            categoryId = 1,
            oldPrice = 1500,
            discountPrice = 999,
            rating = 270,
            isAddedToBookmark = false,
            publishedDate = "Published today at 10:30 PM",
            validDate = "",
            promocode = "GIZ777",
        )
    }
}