package com.codingtroops.screentracker

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ScreenTracker.initialize(
            application = this,
            configuration =
            TrackerConfiguration.Builder()
                .setIsTrackingFragments(true)
                .setIsFilteringLibFragments(false)
                .setTextSize(25f)
                .setTextHexColor("#FFA500")
                .setTextGravity(TrackerTextGravity.TOP)
                .setTextBackgroundHexColor("#FFFF00")
                .build()
        )
    }
}