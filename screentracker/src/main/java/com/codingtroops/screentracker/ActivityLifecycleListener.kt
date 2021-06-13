package com.codingtroops.screentracker

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
//import com.google.android.material.bottomsheet.BottomSheetDialog
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActivityCycleListener(application: Application) {

    fun printFragments(supportFragmentManager: FragmentManager?) {
        if (supportFragmentManager != null) {
//            printFragmentDetails(supportFragmentManager)
//
//            supportFragmentManager.addOnBackStackChangedListener {
//                printFragmentDetails(supportFragmentManager)
//            }
            supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    Log.d("CURRENT_FRAGMENT", f?.javaClass?.simpleName
                        ?: "Fragment has no name found")
                    Log.d("CURRENT_FRAGMENT", "is kotlin: " + f.javaClass.declaredAnnotations.any {  it.annotationClass == Metadata::class })
                }

                override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentViewDestroyed(fm, f)
//                    if(f is BottomSheetDialogFragment || f is DialogFragment || f is BottomSheetDialog) {
//                        Log.d("CURRENT_FRAGMENT_DESTR", "Bottom/Dialog destroyed. Might want to revert to previous fragment")
//                    }
                    if (fm.backStackEntryCount > 0) {
                        val indexOfFragmentBelowPopped = fm.backStackEntryCount - 1
                        val fragmentBelowPopped = fm.fragments[indexOfFragmentBelowPopped]
                        Log.d("CURRENT_FRAGMENT_DESTR",
                            fragmentBelowPopped?.javaClass?.simpleName
                                ?: "Fragment has no name found"
                        )
                    }
                }

            }, true)
        }
    }

    init {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

                with(activity as AppCompatActivity?) {
                    if (this != null) {
                        Log.d("CURRENT_ACTIVITY", (this.javaClass.simpleName ?: "Activity name not found") + "Delete any fragment text * might be leftover *")
                        printFragments(this.supportFragmentManager)
//                        val childManager = this.supportFragmentManager.primaryNavigationFragment?.childFragmentManager
//                        printFragments(childManager)
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