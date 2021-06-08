package com.codingtroops.screentracker

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startForegroundService
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner


object ScreenTracker {

    private lateinit var application: Application

    private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469

    fun initialize(application: Application) {
        val overlayService = Intent(application, TextOverlayService::class.java)
        bindOverlayOnAppEvents(application, overlayService)
        ScreenTracker.application = application
        launchService(application, overlayService)
        bindComponentsListeners(application)
    }

    private fun launchService(
        application: Application,
        overlayService: Intent
    ) {
        val permissionIsRequired = requiresPermissions(application)
        if (!permissionIsRequired) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(application, overlayService)
            else
                application.startService(overlayService)
        }
    }

    private fun bindOverlayOnAppEvents(
        application: Application,
        overlayService: Intent
    ) {
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onAppToForeground() {
                launchService(application, overlayService)
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun onAppToBackground() {
                application.stopService(overlayService)
            }
        })
    }

    private fun requiresPermissions(application: Application) =
        Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(application)

    private fun sendComponentsDetails(activity: Activity, supportFragmentManager: FragmentManager?) {
        if (supportFragmentManager != null) {
            sendDetails(activity, supportFragmentManager)
            supportFragmentManager.addOnBackStackChangedListener {
                sendDetails(activity, supportFragmentManager)
            }
        }
    }

    private fun sendDetails(activity: Activity, supportFragmentManager: FragmentManager) {
        for (fragment in supportFragmentManager.fragments) {
            TextOverlayService.setText(
                application,
                activity.javaClass.simpleName,
                fragment?.javaClass?.simpleName
            )
            sendComponentsDetails(activity, fragment?.childFragmentManager)
        }
    }

    private fun bindComponentsListeners(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (requiresPermissions(application)) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + application.packageName)
                    )
                    activity.startActivityForResult(
                        intent,
                        ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE
                    )
                }
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {
                with(activity as AppCompatActivity?) {
                    if (this != null) {
                        sendComponentsDetails(activity, this.supportFragmentManager)
                        val childManager =
                            this.supportFragmentManager.primaryNavigationFragment?.childFragmentManager
                        sendComponentsDetails(activity, childManager)
                    }
                }
            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }

}