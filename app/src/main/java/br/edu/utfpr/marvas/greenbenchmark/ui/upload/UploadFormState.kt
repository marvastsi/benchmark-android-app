package br.edu.utfpr.marvas.greenbenchmark.ui.upload

/**
 * Data validation state of the upload form.
 */
data class UploadFormState(
    val fileNameError: Int? = null,
    val isDataValid: Boolean = false
)