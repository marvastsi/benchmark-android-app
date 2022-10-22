package br.edu.utfpr.marvas.greenbenchmark.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.edu.utfpr.marvas.greenbenchmark.data.AccountDataSource
import br.edu.utfpr.marvas.greenbenchmark.data.AccountRepository

/**
 * ViewModel provider factory to instantiate AccountViewModel.
 * Required given AccountViewModel has a non-empty constructor
 */
class AccountViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountViewModel::class.java)) {
            return AccountViewModel(
                accountRepository = AccountRepository(
                    dataSource = AccountDataSource()
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}