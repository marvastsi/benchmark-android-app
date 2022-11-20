package br.edu.utfpr.marvas.greenbenchmark.ui.config

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import br.edu.utfpr.marvas.greenbenchmark.commons.ConfigStorage
import br.edu.utfpr.marvas.greenbenchmark.data.ConfigRepository

class ConfigViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(ConfigViewModel::class.java)) {
            return ConfigViewModel(
                configRepository = ConfigRepository(
                    ConfigStorage(
                        context.getSharedPreferences(
                            ConfigStorage.TEST_CONFIG,
                            Context.MODE_PRIVATE
                        )
                    )
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}