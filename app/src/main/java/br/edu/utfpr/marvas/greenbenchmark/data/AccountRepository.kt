package br.edu.utfpr.marvas.greenbenchmark.data

import br.edu.utfpr.marvas.greenbenchmark.data.model.Account

/**
 * Class that requests account information from the remote data source and
 * maintains an in-memory cache of account information.
 */

class AccountRepository(private val dataSource: AccountDataSource) {

    // in-memory cache of the account object
    var account: Account? = null
        private set

    val accountIsPresent: Boolean
        get() = account != null

    init {
        account = null
    }

    fun save(account: Account): Result<Account> {
        // handle save
        val result = dataSource.save(account)

        if (result is Result.Success) {
            setSavedAccount(result.data)
        }

        return result
    }

    fun find(accountId: String): Result<Account> {
        val result = dataSource.find(accountId)

        if (result is Result.Success) {
            account = result.data
        }
        return result
    }

    private fun setSavedAccount(savedAccount: Account) {
        this.account = savedAccount
    }
}