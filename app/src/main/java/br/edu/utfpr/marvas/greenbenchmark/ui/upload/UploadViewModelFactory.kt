package br.edu.utfpr.marvas.greenbenchmark.ui.upload

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.CredentialStorage
import br.edu.utfpr.marvas.greenbenchmark.data.UploadRepository

/**
 * ViewModel provider factory to instantiate UploadViewModel.
 * Required given UploadViewModel has a non-empty constructor
 */
class UploadViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            val baseUrl = context.getString(R.string.base_url)
            val path = context.getString(R.string.file_download_uri)
            return UploadViewModel(
                uploadRepository = UploadRepository(
                    CredentialStorage(
                        context.getSharedPreferences(
                            CredentialStorage.API_PREFERENCES,
                            Context.MODE_PRIVATE
                        )
                    ),
                    baseUrl.plus(path)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}