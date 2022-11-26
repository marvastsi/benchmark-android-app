package br.edu.utfpr.marvas.greenbenchmark.commons

import android.content.SharedPreferences
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config

class ConfigStorage(
    private val sharedPreferences: SharedPreferences
) {

    fun save(config: Config) {
        with(sharedPreferences.edit()) {
            putString(MEDIA_URI_PARAM, config.mediaUri)
            putString(UPLOAD_URI_PARAM, config.uploadUri)
            putLong(LOAD_PARAM, config.testLoad)
            putString(DOWNLOAD_URI_PARAM, config.downloadUri)
            putString(SERVER_URL_PARAM, config.serverUrl)
            apply()
        }
    }

    fun getConfig(): Config {
        val prefs = sharedPreferences.all
        return Config(
            testLoad = prefs[LOAD_PARAM] as Long,
            mediaUri = prefs[MEDIA_URI_PARAM] as String,
            uploadUri = prefs[UPLOAD_URI_PARAM] as String,
            downloadUri = prefs[DOWNLOAD_URI_PARAM] as String,
            serverUrl = prefs[SERVER_URL_PARAM] as String,
        )
    }

    companion object {
        const val TEST_CONFIG = "TEST_CONFIG"
        const val MEDIA_URI_PARAM = "mediaUri"
        const val UPLOAD_URI_PARAM = "uploadUri"
        const val DOWNLOAD_URI_PARAM = "downloadUri"
        const val SERVER_URL_PARAM = "serverUrl"
        const val LOAD_PARAM = "loadConfig"
    }
}

