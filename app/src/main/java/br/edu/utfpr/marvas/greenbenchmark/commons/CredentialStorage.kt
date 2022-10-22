package br.edu.utfpr.marvas.greenbenchmark.commons

import android.content.SharedPreferences

class CredentialStorage(
    private val sharedPreferences: SharedPreferences
) {

    fun saveToken(token: Token) {
        //sharedPreferences
        //        .edit()
        //        .put(API_TOKEN, token.value)
        //        .commit()
        with(sharedPreferences.edit()) {
            putString(API_TOKEN, token.value)
            remove(API_TOKEN)
            apply()
        }
    }

    fun deleteToken() {
        with(sharedPreferences.edit()) {
            remove(API_TOKEN)
            apply()
        }
    }

    fun getToken(): Token {
        val value = sharedPreferences.getString("API_TOKEN", "")!!
        return Token(value = value)
    }

    companion object {
        const val API_TOKEN = "API_TOKEN"
        const val API_PREFERENCES = "API_PREFERENCES"
    }
}

data class Token(val type: String = "Bearer", val value: String)