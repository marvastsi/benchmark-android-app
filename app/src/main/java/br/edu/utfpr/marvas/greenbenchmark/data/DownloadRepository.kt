package br.edu.utfpr.marvas.greenbenchmark.data

import android.util.Log
import br.edu.utfpr.marvas.greenbenchmark.commons.CredentialStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.data.model.DownloadFile
import br.edu.utfpr.marvas.greenbenchmark.http.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadRepository(
    private val credentialStorage: CredentialStorage,
    private val serverUrl: String
) {

    suspend fun download(filePath: String): Result<DownloadFile> {
        return withContext(Dispatchers.IO) {
            val token = credentialStorage.getToken()
            val client = HttpClient()
            try {
                val response = client.getFile(
                    serverUrl,
                    filePath,
                    HttpClient.createDefaultHeader(authorization = token.toString())
                ).body!!
                Log.d(Tags.DOWNLOAD_FILE, response.toString())
                Result.Success(DownloadFile(response))
            } catch (ex: Exception) {
                Log.e(Tags.DOWNLOAD_FILE, "${ex.message}")
                Result.Error(ex)
            }
        }
    }
}