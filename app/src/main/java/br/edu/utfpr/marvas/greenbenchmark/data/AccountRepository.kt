package br.edu.utfpr.marvas.greenbenchmark.data

import android.util.Log
import br.edu.utfpr.marvas.greenbenchmark.commons.CredentialStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.data.model.Account
import br.edu.utfpr.marvas.greenbenchmark.http.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountRepository(
    private val credentialStorage: CredentialStorage,
    private val serverUrl: String
) {

    suspend fun save(account: Account): Result<Account> {
        return withContext(Dispatchers.IO) {
            val token = credentialStorage.getToken()
            val client = HttpClient()
            try {
                val response = client.post(
                    serverUrl,
                    account,
                    Account::class.java,
                    HttpClient.createDefaultHeader(authorization = token.toString())
                ).body!!
                Log.d(Tags.ACCOUNT_FORM, response.toString())
                Result.Success(response)
            } catch (ex: Exception) {
                Log.e(Tags.ACCOUNT_FORM, "${ex.message}")
                Result.Error(ex)
            }
        }
    }
}