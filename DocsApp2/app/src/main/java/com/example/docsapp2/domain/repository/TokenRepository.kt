package com.example.docsapp2.domain.repository

interface TokenRepository {
    fun saveToken(token: String)

    fun getToken(): String

    fun deleteToken()
}