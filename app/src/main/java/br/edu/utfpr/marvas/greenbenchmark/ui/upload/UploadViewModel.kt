package br.edu.utfpr.marvas.greenbenchmark.ui.upload

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.data.Result
import br.edu.utfpr.marvas.greenbenchmark.data.UploadRepository
import kotlinx.coroutines.launch
import java.io.File

class UploadViewModel(
    private val uploadRepository: UploadRepository
) : ViewModel() {

    private val _uploadForm = MutableLiveData<UploadFormState>()
    val uploadFormState: LiveData<UploadFormState> = _uploadForm

    private val _uploadFileResult = MutableLiveData<UploadFileResult>()
    val uploadResult: LiveData<UploadFileResult> = _uploadFileResult

    fun upload(filePath: String) {
        viewModelScope.launch {
            when (val result = uploadRepository.upload(filePath)) {
                is Result.Success ->
                    _uploadFileResult.postValue(
                        UploadFileResult(
                            success = UploadFileView(result.data)
                        )
                    )
                else -> _uploadFileResult.postValue(UploadFileResult(error = R.string.upload_failed))
            }
        }
    }

    fun uploadDataChanged(
        fileName: String
    ) {
        if (fileName.isBlank()) {
            _uploadForm.value = UploadFormState(fileNameError = R.string.invalid_file)
        } else {
            _uploadForm.value = UploadFormState(isDataValid = true)
        }
    }
}