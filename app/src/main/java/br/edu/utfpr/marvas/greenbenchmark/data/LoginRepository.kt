package br.edu.utfpr.marvas.greenbenchmark.data

import android.util.Log
import br.edu.utfpr.marvas.greenbenchmark.commons.CredentialStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.commons.Token
import br.edu.utfpr.marvas.greenbenchmark.data.model.Credentials
import br.edu.utfpr.marvas.greenbenchmark.data.model.LoggedInUser
import br.edu.utfpr.marvas.greenbenchmark.http.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class LoginRepository(
    private val credentialStorage: CredentialStorage,
    private val loginUrl: String
) {

    fun logout() {
        credentialStorage.deleteToken()
    }

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        return withContext(Dispatchers.IO) {

            val client = HttpClient()
            try {
                val token = client.post(
                    loginUrl,
                    Credentials(username, password),
                    Token::class.java,
                    HttpClient.createDefaultHeader()
                ).body!!
                Log.d(Tags.LOGIN, token.toString())
                credentialStorage.saveToken(token)
                // Should obtain userId from token decoded
                Result.Success(LoggedInUser(UUID.randomUUID().toString(), username))
            } catch (ex: Exception) {
                Log.e(Tags.LOGIN, "${ex.message}")
                Result.Error(ex)
            }
        }
    }
}