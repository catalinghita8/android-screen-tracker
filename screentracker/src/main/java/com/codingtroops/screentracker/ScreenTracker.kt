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

        ScreenTracker.application = application
        val requirePermission = launchService(application, overlayService)
        bindScreenActivity(application, requirePermission)
    }

    private fun launchService(
        application: Application,
        overlayService: Intent
    ): Boolean {
        val requirePermission =
            Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(application)
        if (!requirePermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(application, overlayService)
            else
                application.startService(overlayService)
        }
        return requirePermission
    }

    private fun printFragments(supportFragmentManager: FragmentManager?) {
        if (supportFragmentManager != null) {
            printFragmentDetails(supportFragmentManager)
            supportFragmentManager.addOnBackStackChangedListener {
                printFragmentDetails(supportFragmentManager)
            }
        }
    }

    private fun printFragmentDetails(supportFragmentManager: FragmentManager) {
        for (fragment in supportFragmentManager.fragments) {
            TextOverlayService.setText(application, fragment?.javaClass?.simpleName)
            printFragments(fragment?.childFragmentManager)
        }
    }

    private fun bindScreenActivity(application: Application, requirePermission: Boolean) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (requirePermission) {
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
                        printFragments(this.supportFragmentManager)
                        val childManager =
                            this.supportFragmentManager.primaryNavigationFragment?.childFragmentManager
                        printFragments(childManager)
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