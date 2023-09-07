package com.digeltech.discountone.util.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.digeltech.discountone.MainActivity
import com.digeltech.discountone.R

fun showNotification(context: Context, title: String?, message: String?, data: Map<String, String>) {
    val channelId = context.getString(R.string.default_notification_channel_id)
    val channelName = context.getString(R.string.default_notification_channel_name)

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

    val pendingIntent: PendingIntent = NavDeepLinkBuilder(context)
        .setGraph(R.navigation.mobile_navigation)
        .setDestination(destination)
        .setArguments(args)
        .setComponentName(MainActivity::class.java)
        .createPendingIntent()

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(R.drawable.ic_launcher)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
    notificationManager.createNotificationChannel(channel)

    notificationManager.notify(1, notificationBuilder.build())
}

fun openNotification(fragment: Fragment, data: Map<String, String>) {
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

    findNavController(fragment).navigate(destination, args)
}
