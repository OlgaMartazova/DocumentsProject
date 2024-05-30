package com.example.docsapp2.data.database.local

import android.content.SharedPreferences

class PreferenceManager(
    private var sharedPreferences: SharedPreferences
) {
    fun storeToken(idToken: String) {
        sharedPreferences.edit().putString(TOKEN_PREF, idToken).apply()
    }

    fun retrieveToken(): String? {
        return sharedPreferences.getString(TOKEN_PREF, DEFAULT_VALUE)
    }

    fun deleteToken() {
        //sharedPreferences.edit().putString(TOKEN_PREF, DEFAULT_VALUE).apply()
        sharedPreferences.edit().remove(TOKEN_PREF).apply()
    }

    companion object {
        private const val TOKEN_PREF = "token"
        private val DEFAULT_VALUE = null
        private const val DEFAULT_VALUE_INT = 0
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
    }
}