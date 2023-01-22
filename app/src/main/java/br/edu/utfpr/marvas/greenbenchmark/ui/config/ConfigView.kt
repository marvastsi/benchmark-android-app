package br.edu.utfpr.marvas.greenbenchmark.ui.config

import br.edu.utfpr.marvas.greenbenchmark.data.model.Config

data class ConfigView(
    val testLoad: Long,
    val mediaUri: String,
    val uploadUri: String,
    val downloadUri: String,
    val serverUrl: String
) {
    companion object {
        fun fromModel(config: Config) = ConfigView(
            testLoad = config.testLoad,
            mediaUri = config.mediaUri,
            uploadUri = config.uploadUri,
            downloadUri = config.downloadUri,
            serverUrl = config.serverUrl
        )
    }
}