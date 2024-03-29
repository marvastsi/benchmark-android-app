package br.edu.utfpr.marvas.greenbenchmark.ui.config

data class ConfigFormState(
    val testLoadError: Int? = null,
    val mediaUriError: Int? = null,
    val uploadUriError: Int? = null,
    val downloadUriError: Int? = null,
    val serverUrlError: Int? = null,
    val isDataValid: Boolean = false
)