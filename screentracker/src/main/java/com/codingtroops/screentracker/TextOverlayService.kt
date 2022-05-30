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
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class TextOverlayService : Service() {

    private var overlayTextView: TextView? = null

    override fun onBind(intent: Intent?): Nothing? = null

    override fun onCreate() {
        super.onCreate()
        setupTextView()
        addTextView()
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(messageReceiver, IntentFilter(ACTION_LISTEN_TO_TRACKER))
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager
            .getInstance(this)
            .unregisterReceiver(messageReceiver)
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.removeView(overlayTextView)
        overlayTextView = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        intent?.let { parseIntent(it) }
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
        val notification = notificationBuilder
            .setOngoing(true)
            .setPriority(PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    private fun parseIntent(intent: Intent) {
        // Get extra data included in the Intent
        val config = intent.getParcelableExtra<TrackerConfiguration>(EXTRA_CONFIG)
        if (config != null && config != configuration) {
            configuration = config
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.removeView(overlayTextView)
            overlayTextView = null
            setupTextView()
            addTextView()
        }
        val activityClassName = intent.getStringExtra(EXTRA_ACTIVITY_TEXT)
        val fragmentClassName = intent.getStringExtra(EXTRA_FRAGMENT_TEXT)
        if (activityClassName != null || fragmentClassName != null)
            overlayTextView?.text = getDisplayText(activityClassName, fragmentClassName)
    }

    private fun setupTextView() {
        overlayTextView = TextView(this)
        val backgroundColor = Color.parseColor(configuration.textBackgroundColor)
        val textColor = Color.parseColor(configuration.textHexColor)
        val textView = overlayTextView
        if (textView != null)
            with(textView) {
                textSize = configuration.textSize
                setTextColor(textColor)
                setBackgroundColor(backgroundColor)
                gravity = Gravity.CENTER_HORIZONTAL
                text = lastUsedOverlayText
            }
    }

    private fun addTextView() {
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
        params.gravity = Gravity.CENTER or configuration.gravity.value
        params.title = "Text Overlay"
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(overlayTextView, params)
    }

    private val messageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            parseIntent(intent)
        }
    }

    /**
     * Helper method that simplifies updating overlay text.
     *
     * @param context A context.
     * @param text The new overlay text.
     */
    companion object {
        private val ACTION_LISTEN_TO_TRACKER =
            "com.codingtroops.screentracker.ACTION_LISTEN_TO_TRACKER"
        private val EXTRA_ACTIVITY_TEXT = "com.codingtroops.screentracker.activity_text"
        private val EXTRA_FRAGMENT_TEXT = "com.codingtroops.screentracker.fragment_text"
        private val EXTRA_CONFIG = "com.codingtroops.screentracker.config"


        private var lastUsedOverlayText: String? = null
        private var configuration = TrackerConfiguration.DEFAULT

        fun setConfiguration(context: Context, configuration: TrackerConfiguration) {
            val intent = Intent(ACTION_LISTEN_TO_TRACKER)
            intent.putExtra(EXTRA_CONFIG, configuration)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }

        fun setText(
            context: Context,
            activityClassName: String?,
            fragmentClassName: String?,
            configuration: TrackerConfiguration
        ) {
            val intent = Intent(ACTION_LISTEN_TO_TRACKER)
            intent.putExtra(EXTRA_ACTIVITY_TEXT, activityClassName)
            intent.putExtra(EXTRA_FRAGMENT_TEXT, fragmentClassName)
            intent.putExtra(EXTRA_CONFIG, configuration)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            lastUsedOverlayText = getDisplayText(activityClassName, fragmentClassName)
            this.configuration = configuration
        }

        fun getDisplayText(activityClassName: String?, fragmentClassName: String?): String {
            var text = "$activityClassName"
            if (fragmentClassName != null)
                text += " > $fragmentClassName"
            return text
        }

    }

}