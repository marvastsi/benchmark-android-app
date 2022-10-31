package br.edu.utfpr.marvas.greenbenchmark.ui.download

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.data.DownloadRepository
import br.edu.utfpr.marvas.greenbenchmark.data.Result
import kotlinx.coroutines.launch

class DownloadViewModel(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _downloadForm = MutableLiveData<DownloadFormState>()
    val downloadFormState: LiveData<DownloadFormState> = _downloadForm

    private val _downloadFileResult = MutableLiveData<DownloadFileResult>()
    val downloadResult: LiveData<DownloadFileResult> = _downloadFileResult

    fun download(fileName: String) {
        viewModelScope.launch {
            when (val result = downloadRepository.download(fileName)) {
                is Result.Success ->
                    _downloadFileResult.postValue(
                        DownloadFileResult(
                            success = DownloadFileView(result.data)
                        )
                    )
                else -> _downloadFileResult.postValue(DownloadFileResult(error = R.string.download_failed))
            }
        }
    }

    fun downloadDataChanged(
        fileName: String
    ) {
        if (fileName.isBlank()) {
            _downloadForm.value = DownloadFormState(fileNameError = R.string.invalid_file)
        } else {
            _downloadForm.value = DownloadFormState(isDataValid = true)
        }
    }
}