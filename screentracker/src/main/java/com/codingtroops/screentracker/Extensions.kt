package com.codingtroops.screentracker

import android.app.Activity
import android.app.Application
import android.os.Bundle


fun Application.registerPartialActivityLifecycleCallbacks(
    onActivityCreated: (activity: Activity) -> Unit,
    onActivityResumed: (activity: Activity) -> Unit
) {
    this.registerActivityLifecycleCallbacks(object :
        Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            onActivityCreated(activity)
        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityResumed(activity: Activity) {
            onActivityResumed(activity)
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