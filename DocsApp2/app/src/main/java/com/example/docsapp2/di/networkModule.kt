package com.example.docsapp2.di

import com.example.docsapp2.data.api.AuthInterceptor
import com.example.docsapp2.data.api.DocsApi
import de.hdodenhof.circleimageview.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://dcb8-188-225-109-79.ngrok-free.app/"

//private const val BASE_URL = "https://192.168.0.100:5000/"
private const val TYPE_HEADER = "Content-Type"
private const val JSON_TYPE = "application/json"

val networkModule = module {
    factory<Interceptor> { AuthInterceptor(tokenRepository = get()) }

    factory { provideOkHttpClient(interceptor = get()) }

    factory { provideRetrofit(okHttpClient = get()) }

    single { provideDocsApi(retrofit = get()) }
}

fun provideDocsApi(retrofit: Retrofit): DocsApi =
    retrofit.create(DocsApi::class.java)

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideOkHttpClient(
    interceptor: Interceptor,
): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.MINUTES)
        .writeTimeout(2, TimeUnit.MINUTES) // write timeout
        .readTimeout(2, TimeUnit.MINUTES) // read timeout
        .addInterceptor(typeHeaderInterceptor())
        .addInterceptor(interceptor)
        .also {
            if (BuildConfig.DEBUG) {
                it.addInterceptor(
                    HttpLoggingInterceptor()
                        .setLevel(
                            HttpLoggingInterceptor.Level.BODY
                        )
                )
            }
        }
        .build()
}

private fun typeHeaderInterceptor() = Interceptor { chain ->
    val original = chain.request()
    chain.proceed(
        original.newBuilder()
            .header(
                TYPE_HEADER, JSON_TYPE
            )
            .build()
    )
}