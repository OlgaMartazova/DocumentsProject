package com.example.docsapp2.di

import com.example.docsapp2.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule =
    module {
        factoryOf(::UploadFileUseCase)
        factoryOf(::VerifyTokenUseCase)
        factoryOf(::StartPackageUseCase)
        factoryOf(::EndPackageUseCase)
        factoryOf(::SignOutUseCase)
        factoryOf(::ProcessResultUseCase)
        factory { Dispatchers.Default }
    }
