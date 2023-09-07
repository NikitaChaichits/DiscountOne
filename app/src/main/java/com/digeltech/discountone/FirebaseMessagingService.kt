package com.digeltech.discountone

import com.digeltech.discountone.ui.common.addNotificationToCache
import com.digeltech.discountone.util.notifications.showNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        addNotificationToCache(
            title = remoteMessage.notification?.title ?: "",
            text = remoteMessage.notification?.body ?: "",
            data = remoteMessage.data
        )

        showNotification(
            context = applicationContext,
            title = remoteMessage.notification?.title,
            message = remoteMessage.notification?.body,
            data = remoteMessage.data
        )
    }

    override fun onNewToken(token: String) = Unit

}