package br.edu.utfpr.marvas.greenbenchmark.ui.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository
import br.edu.utfpr.marvas.greenbenchmark.data.Result
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config
import kotlinx.coroutines.launch

class ConfigViewModel(private val configRepository: ConfigRepository) : ViewModel() {

    private val _configForm = MutableLiveData<ConfigFormState>()
    val configFormState: LiveData<ConfigFormState> = _configForm

    private val _configResult = MutableLiveData<ConfigResult>()
    val configResult: LiveData<ConfigResult> = _configResult

    fun saveConfig(config: Config) {
        viewModelScope.launch {
            when (val result = configRepository.saveConfig(config)) {
                is Result.Success -> _configResult.postValue(
                    ConfigResult(
                        success = ConfigView.fromModel(result.data)
                    )
                )
                else -> _configResult.postValue(ConfigResult(error = R.string.config_failed))
            }
        }
    }

    fun configDataChanged(
        load: String,
        mediaUri: String,
        uploadUri: String,
        downloadUri: String,
        serverUrl: String
    ) {
        if (load.isBlank() || load.toLong() < 10) {
            _configForm.value = ConfigFormState(testLoadError = R.string.invalid_test_load)
        } else if (mediaUri.isBlank()) {
            _configForm.value = ConfigFormState(mediaUriError = R.string.invalid_media_uri)
        } else if (uploadUri.isBlank()) {
            _configForm.value = ConfigFormState(uploadUriError = R.string.invalid_upload_uri)
        } else if (downloadUri.isBlank()) {
            _configForm.value = ConfigFormState(downloadUriError = R.string.invalid_download_uri)
        } else if (serverUrl.isBlank()) {
            _configForm.value = ConfigFormState(serverUrlError = R.string.invalid_server_url)
        } else {
            _configForm.value = ConfigFormState(isDataValid = true)
        }
    }
}