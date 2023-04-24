package com.arya.submission3

import android.app.Application
import com.google.android.material.color.DynamicColors

class StoryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}