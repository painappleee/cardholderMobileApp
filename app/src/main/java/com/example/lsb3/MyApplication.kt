package com.example.lsb3

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        private var appContext: Context? = null

    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
