package com.codingtroops.screentracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class TextOverlayService : Service() {

    private var textView: TextView? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        textView = TextView(this)
        val backgroundColor = Color.parseColor("#A1FFFFFF")
        val textColor = Color.parseColor("#000000")
        textView!!.setTextColor(textColor)
        textView!!.setBackgroundColor(backgroundColor)
        textView!!.gravity = Gravity.CENTER_HORIZONTAL
        textView!!.text = lastUsedOverlayText

        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER or Gravity.BOTTOM
        params.title = "Text Overlay"
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(textView, params)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(messageReceiver, IntentFilter(ACTION_SET_TEXT))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.removeView(textView)
        textView = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        intent?.let { getTextFromIntent(it) }
        return START_NOT_STICKY
    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("text_overlay_service", "Text Overlay Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    private fun getTextFromIntent(intent: Intent) {
        // Get extra data included in the Intent
        val message = intent.getStringExtra(EXTRA_TEXT)
        if (message != null) {
            textView!!.text = message
        }
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            getTextFromIntent(intent)
        }
    }

    /**
     * Helper method that simplifies updating overlay text.
     *
     * @param context A context.
     * @param text The new overlay text.
     */
    companion object {
        private val ACTION_SET_TEXT = "com.codingtroops.screentracker.SET_TEXT"
        private val EXTRA_TEXT = "com.codingtroops.screentracker.text"
        private val TAG: String = TextOverlayService::javaClass.name
        private var lastUsedOverlayText: String? = null

        fun setText(context: Context, text: String?) {
            val intent = Intent(ACTION_SET_TEXT)
            intent.putExtra(EXTRA_TEXT, text)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            lastUsedOverlayText = text
        }

    }

}