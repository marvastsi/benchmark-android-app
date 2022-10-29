package br.edu.utfpr.marvas.greenbenchmark.ui.download

/**
 * Data validation state of the download form.
 */
data class DownloadFormState(
    val fileNameError: Int? = null,
    val isDataValid: Boolean = false
)