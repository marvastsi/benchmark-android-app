package br.edu.utfpr.marvas.greenbenchmark.data

import android.content.ContentResolver
import android.util.Log
import br.edu.utfpr.marvas.greenbenchmark.commons.CredentialStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.data.model.UploadFile
import br.edu.utfpr.marvas.greenbenchmark.http.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UploadRepository(
    private val credentialStorage: CredentialStorage,
    private val contentResolver: ContentResolver,
    private val serverUrl: String
) {

    suspend fun upload(filePath: String): Result<UploadFile> {
        return withContext(Dispatchers.IO) {
            val token = credentialStorage.getToken()
            val client = HttpClient()
            try {
                val response = client.postFile(
                    serverUrl,
                    filePath,
                    contentResolver,
                    UploadFile::class.java,
                    HttpClient.createDefaultHeader(authorization = token.toString())
                ).body!!
                Log.d(Tags.UPLOAD_FILE, response.toString())
                Result.Success(response)
            } catch (ex: Exception) {
                Log.e(Tags.UPLOAD_FILE, "${ex.message}", ex)
                Result.Error(ex)
            }
        }
    }
}