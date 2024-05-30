package com.example.docsapp2.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import com.example.docsapp2.data.mapper.Mapper
import com.example.docsapp2.data.impl.*
import org.koin.core.module.dsl.bind
import com.example.docsapp2.domain.repository.DocsRepository
import com.example.docsapp2.data.database.local.PreferenceManager
import com.example.docsapp2.domain.repository.TokenRepository
import org.koin.android.ext.koin.androidApplication

val repositoryModule =
    module {
        factoryOf(::PreferenceManager)
        factoryOf(::Mapper)
        factoryOf(::DocsRepositoryImpl) { bind<DocsRepository>() }
        factoryOf(::TokenRepositoryImpl) { bind<TokenRepository>() }
    }

val sharedPreferencesModule = module {
    single { provideSharedPref(androidApplication()) }
}

fun provideSharedPref(app: Application): SharedPreferences {
    return app.applicationContext.getSharedPreferences(
        "shared_preferences",
        Context.MODE_PRIVATE
    )
}