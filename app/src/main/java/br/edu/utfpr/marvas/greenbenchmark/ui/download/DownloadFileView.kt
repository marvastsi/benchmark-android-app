package br.edu.utfpr.marvas.greenbenchmark.ui.download

import br.edu.utfpr.marvas.greenbenchmark.data.model.DownloadFile

data class DownloadFileView(
    val fileView: DownloadFile,
) {
    fun fileName(): String = fileView.file.name
}
