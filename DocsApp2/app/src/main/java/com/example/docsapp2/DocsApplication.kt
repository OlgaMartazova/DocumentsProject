package com.example.docsapp2

import android.app.Application
import android.content.Context
import com.example.docsapp2.di.networkModule
import com.example.docsapp2.di.repositoryModule
import com.example.docsapp2.di.sharedPreferencesModule
import com.example.docsapp2.di.useCaseModule
import com.example.docsapp2.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DocsApplication: Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        startKoin {
            allowOverride(false)
            androidContext(this@DocsApplication)
            modules(
                listOf(
                    viewModelModule,
                    useCaseModule,
                    repositoryModule,
                    networkModule,
                    sharedPreferencesModule,
                )
            )
        }
    }
}