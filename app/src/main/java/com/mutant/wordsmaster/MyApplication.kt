package com.mutant.wordsmaster

import android.app.Application
import com.mutant.wordsmaster.util.trace.DebugHelper

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            DebugHelper.setDebugEnabled(true)
        }
    }
}