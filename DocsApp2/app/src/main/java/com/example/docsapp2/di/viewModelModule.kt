package com.example.docsapp2.di

import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModelOf
import com.example.docsapp2.presentation.viewmodel.*

val viewModelModule = module {
    viewModelOf(::ResultViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::MainViewModel)
}