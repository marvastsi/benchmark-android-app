package br.edu.utfpr.marvas.greenbenchmark.data

import android.util.Log
import br.edu.utfpr.marvas.greenbenchmark.commons.CredentialStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.commons.Token
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import br.edu.utfpr.marvas.greenbenchmark.data.model.Credentials
import br.edu.utfpr.marvas.greenbenchmark.data.model.LoggedInUser
import br.edu.utfpr.marvas.greenbenchmark.http.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class ConfigRepository {

    suspend fun loadConfig(testLoad: String): Result<Config> {
        return withContext(Dispatchers.IO) {
            Log.d(Tags.CONFIG, "testLoad: $testLoad")
            if(testLoad.isNotBlank() && testLoad.toLong() >= 10) {
                Result.Success(Config(testLoad.toLong()))
            } else {
                Result.Error(Exception("Invalid test load value: $testLoad"))
            }
        }
    }
}