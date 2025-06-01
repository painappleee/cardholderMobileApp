package com.example.lsb3

import android.app.Application
import com.example.lsb3.data.database.DataBaseManager
import com.example.lsb3.ui.viewmodel.AddEditCardViewModel
import com.example.lsb3.ui.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val appModule = module {
            single { DataBaseManager(androidContext()) }
            viewModel { AddEditCardViewModel(get()) }
            viewModel{MainViewModel(get())}
        }

        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }

    }
}
