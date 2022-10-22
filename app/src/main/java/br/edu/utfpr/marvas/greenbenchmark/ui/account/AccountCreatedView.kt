package br.edu.utfpr.marvas.greenbenchmark.ui.account

import br.edu.utfpr.marvas.greenbenchmark.data.model.Account

/**
 * User details post authentication that is exposed to the UI
 */
data class AccountCreatedView(
    val accountId: String,
    val displayName: String,
    val email: String,
    val phoneNumber: String,
    val notification: Boolean,
    val active: Boolean,
    val username: String,
) {
    companion object {
        fun fromAccount(account: Account) = AccountCreatedView(
            accountId = account.accountId,
            displayName = account.firstName + account.lastName,
            email = account.email,
            phoneNumber = "${account.phoneCountryCode} ${account.phoneNumber}",
            notification = account.notification,
            active = account.active,
            username = account.username
        )
    }
}
