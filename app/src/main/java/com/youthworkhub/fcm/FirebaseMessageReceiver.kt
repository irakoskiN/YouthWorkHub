package com.youthworkhub.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.youthworkhub.R
import com.youthworkhub.ui.activity.MainActivity
import com.youthworkhub.utils.Constants
import java.util.*

class FirebaseMessageReceiver : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.notification != null) {
            Log.i("FirebaseMessageReceiver", "onMessageReceived")
            showNotification(
                remoteMessage.notification!!.title,
                remoteMessage.notification!!.body
            )
        }

    }

    private fun showNotification(
        title: String?,
        message: String?
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.NOTIFICATION_TITLE, title)
        intent.putExtra(Constants.NOTIFICATION_MESSAGE, message)

        val pendingIntent = PendingIntent.getActivity(
            this,
            UUID.randomUUID().hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "notification_channel"
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext, channelId
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)

        val notificationChannel = NotificationChannel(
            channelId, "notification", NotificationManager.IMPORTANCE_MIN
        )

        notificationManager!!.createNotificationChannel(
            notificationChannel
        )

        notificationManager.notify(0, builder.build())
    }

}