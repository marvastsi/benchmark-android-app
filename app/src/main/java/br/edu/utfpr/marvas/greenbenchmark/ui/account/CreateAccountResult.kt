package br.edu.utfpr.marvas.greenbenchmark.ui.account

/**
 * Create account result : success (account details) or error message.
 */
data class CreateAccountResult(
    val success: AccountCreatedView? = null,
    val error: Int? = null
)