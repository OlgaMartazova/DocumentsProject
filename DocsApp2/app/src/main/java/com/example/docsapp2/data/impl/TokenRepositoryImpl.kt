package com.example.docsapp2.data.impl

import com.example.docsapp2.data.database.local.PreferenceManager
import com.example.docsapp2.domain.repository.TokenRepository

class TokenRepositoryImpl(
    private val preferenceManager: PreferenceManager
): TokenRepository {
    override fun saveToken(token: String) {
        preferenceManager.storeToken(token)
    }

    override fun getToken(): String {
        return preferenceManager.retrieveToken() ?: DEFAULT_VALUE
    }

    override fun deleteToken() {
        preferenceManager.deleteToken()
    }

    companion object {
        private const val DEFAULT_VALUE = ""
    }
}