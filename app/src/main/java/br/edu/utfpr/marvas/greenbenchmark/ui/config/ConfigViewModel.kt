package br.edu.utfpr.marvas.greenbenchmark.ui.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository
import br.edu.utfpr.marvas.greenbenchmark.data.Result
import kotlinx.coroutines.launch

class ConfigViewModel(private val configRepository: ConfigRepository) : ViewModel() {

    private val _configForm = MutableLiveData<ConfigFormState>()
    val configFormState: LiveData<ConfigFormState> = _configForm

    private val _configResult = MutableLiveData<ConfigResult>()
    val configResult: LiveData<ConfigResult> = _configResult

    fun loadConfig(value: String) {
        viewModelScope.launch {
            when (val result = configRepository.loadConfig(value)) {
                is Result.Success -> _configResult.postValue(
                    ConfigResult(
                        success = ConfigView(
                            testLoad = result.data.testLoad
                        )
                    )
                )
                else -> _configResult.postValue(ConfigResult(error = R.string.config_failed))
            }
        }
    }

    fun configDataChanged(value: String) {
        if (value.isNotBlank() && value.toLong() < 10) {
            _configForm.value = ConfigFormState(testLoadError = R.string.invalid_config)
        } else {
            _configForm.value = ConfigFormState(isDataValid = true)
        }
    }
}