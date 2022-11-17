package br.edu.utfpr.marvas.greenbenchmark.ui.upload

import br.edu.utfpr.marvas.greenbenchmark.data.model.UploadFile

data class UploadFileView(
    val fileView: UploadFile,
) {
    fun fileName(): String = fileView.name
}
