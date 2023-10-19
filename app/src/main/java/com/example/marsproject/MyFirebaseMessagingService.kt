package com.example.marsproject

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "FIREBASE_MESSAGING"

    override fun onNewToken(token: String) {
        //새로운 token이 생성될때마다 호출되는 callback
        Log.d("onNewToken","${token}")
        super.onNewToken(token)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            if ( /* Check if data needs to be processed by long running job */true) {
            } else {
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
            sendNotification(remoteMessage.notification!!.body)
        }
    }


    private fun sendNotification(messageBody: String?) {
        // 첫 번째 Intent: SendMessageActivity로 이동하는 Intent
        val intent1 = Intent(this, SendMessageActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent1 = PendingIntent.getActivity(this, 0, intent1,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        // 두 번째 Intent: 다른 액티비티로 이동하는 Intent
        val intent2 = Intent(this, SearchPeopleActivity::class.java)
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent2 = PendingIntent.getActivity(this, 0, intent2,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = "channelid"

        val defaultSoundUri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder =
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("새로운 메세지입니다")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent1)
        if(messageBody!!.contains("친구 신청을 보냈습니다")){
            notificationBuilder.setContentIntent(pendingIntent2)
        }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "name"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    // 닉네임 정보를 가져오는 함수
    fun getName(): String {
        val pref = getSharedPreferences("userName", 0)
        return pref.getString("name", "").toString()
    }

}