package com.codingtroops.screentracker

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class MyFragmentActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_fragment)
    }
}