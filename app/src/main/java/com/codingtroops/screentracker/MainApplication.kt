package com.codingtroops.screentracker

import android.app.Application

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        ScreenTracker.initialize(this)
    }
}