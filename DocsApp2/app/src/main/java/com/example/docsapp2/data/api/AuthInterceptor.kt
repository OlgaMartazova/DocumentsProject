package com.example.docsapp2.data.api

import com.example.docsapp2.domain.repository.TokenRepository
import okhttp3.Interceptor
import okhttp3.Response

private const val TOKEN_HEADER = "Authorization"
class AuthInterceptor(
    private val tokenRepository: TokenRepository
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        return chain.proceed(
            original.newBuilder()
                .header(
                    TOKEN_HEADER, "Bearer ${tokenRepository.getToken()}"
                )
                .build()
        )
    }
}