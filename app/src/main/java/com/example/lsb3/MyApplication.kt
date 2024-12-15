package com.example.lsb3

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        private var appContext: Context? = null
        lateinit var dbManager: DataBaseManager

    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        dbManager = DataBaseManager(this)

    }
}
