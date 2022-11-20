package br.edu.utfpr.marvas.greenbenchmark.data

import android.util.Log
import br.edu.utfpr.marvas.greenbenchmark.commons.ConfigStorage
import br.edu.utfpr.marvas.greenbenchmark.commons.Tags
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfigRepository(
    private val configStorage: ConfigStorage,
) {

    suspend fun saveConfig(config: Config): Result<Config> {
        return withContext(Dispatchers.IO) {
            Log.d(Tags.CONFIG, "Config[ $config ]")
            try {
                configStorage.save(config)
                Result.Success(config)
            } catch (ex: Exception) {
                Log.e(Tags.CONFIG, "${ex.message}")
                Result.Error(Exception("Error saving config.", ex))
            }
        }
    }

    fun getConfig(): Config {
        return try {
            configStorage.getConfig()
        } catch (ex: Exception) {
            throw Exception("Error on retrieve config.", ex)
        }
    }
}