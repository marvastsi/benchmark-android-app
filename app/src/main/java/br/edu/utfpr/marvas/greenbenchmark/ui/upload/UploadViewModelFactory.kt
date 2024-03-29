package br.edu.utfpr.marvas.greenbenchmark.ui.upload

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.CredentialStorage
import br.edu.utfpr.marvas.greenbenchmark.data.UploadRepository
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config

/**
 * ViewModel provider factory to instantiate UploadViewModel.
 * Required given UploadViewModel has a non-empty constructor
 */
class UploadViewModelFactory(
    private val context: Context,
    private val config: Config
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            val baseUrl = config.serverUrl
            val path = context.getString(R.string.file_upload_uri)
            return UploadViewModel(
                uploadRepository = UploadRepository(
                    CredentialStorage(
                        context.getSharedPreferences(
                            CredentialStorage.API_PREFERENCES,
                            Context.MODE_PRIVATE
                        )
                    ),
                    context.contentResolver,
                    baseUrl.plus(path)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}