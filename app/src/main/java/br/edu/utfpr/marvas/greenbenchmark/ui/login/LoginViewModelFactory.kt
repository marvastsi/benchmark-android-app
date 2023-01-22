package br.edu.utfpr.marvas.greenbenchmark.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.commons.CredentialStorage
import br.edu.utfpr.marvas.greenbenchmark.data.LoginRepository
import br.edu.utfpr.marvas.greenbenchmark.data.model.Config

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory(
    private val context: Context,
    private val config: Config
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val baseUrl = config.serverUrl
            val path = context.getString(R.string.login_uri)
            return LoginViewModel(
                loginRepository = LoginRepository(
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