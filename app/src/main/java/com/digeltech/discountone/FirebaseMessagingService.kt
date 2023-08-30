package com.digeltech.discountone

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body, remoteMessage.data)
    }

    override fun onNewToken(token: String) = Unit

    private fun showNotification(title: String?, message: String?, data: Map<String, String>) {
        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_name)

        val destination = when (data["fragment"]) {
            "ShopsFragment" -> R.id.shopsFragment
            "CategoriesFragment" -> R.id.categoriesFragment
            "CategoryFragment", "ShopFragment" -> R.id.categoryAndShopFragment
            "DealFragment" -> R.id.dealFragment
            else -> R.id.splashFragment
        }

        val args = when (destination) {
            R.id.categoryAndShopFragment -> {
                val id = data["id"]?.toInt() ?: 0
                val title = data["title"] ?: ""
                val slug = data["slug"] ?: ""
                val isFromCategory = data["isFromCategory"].toBoolean()
                Bundle().apply {
                    putInt("id", id)
                    putString("title", title)
                    putString("slug", slug)
                    putBoolean("isFromCategory", isFromCategory)
                }
            }
            R.id.dealFragment -> {
                val id = data["id"]?.toInt() ?: 0
                Bundle().apply {
                    putParcelable("deal", null)
                    putInt("dealId", id)
                }
            }
            else -> null
        }

        val pendingIntent: PendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(destination)
            .setArguments(args)
            .setComponentName(MainActivity::class.java)
            .createPendingIntent()

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(1, notificationBuilder.build())
    }

}