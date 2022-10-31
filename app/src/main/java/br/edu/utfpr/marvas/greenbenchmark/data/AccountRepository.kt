package br.edu.utfpr.marvas.greenbenchmark.data

import android.util.Log
import br.edu.utfpr.marvas.greenbenchmark.commons.CredentialStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.data.model.Account
import br.edu.utfpr.marvas.greenbenchmark.http.HttpClient
import br.edu.utfpr.marvas.greenbenchmark.ui.account.AccountCreatedView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountRepository(
    private val credentialStorage: CredentialStorage,
    private val serverUrl: String
) {

    suspend fun save(account: Account): Result<AccountCreatedView> {
        return withContext(Dispatchers.IO) {
            val token = credentialStorage.getToken()
            val client = HttpClient()
            try {
                val response = client.post(
                    serverUrl,
                    account,
                    AccountCreatedView::class.java,
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