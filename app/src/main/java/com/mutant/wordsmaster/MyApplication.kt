package com.mutant.wordsmaster

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.mutant.wordsmaster.util.trace.DebugHelper
import io.fabric.sdk.android.Fabric

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            DebugHelper.setDebugEnabled(true)
        } else {
            Fabric.with(this, Crashlytics())
        }
    }
}