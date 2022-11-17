package br.edu.utfpr.marvas.greenbenchmark.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository

class ConfigViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ConfigViewModel::class.java)) {
            return ConfigViewModel(
                configRepository = ConfigRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}