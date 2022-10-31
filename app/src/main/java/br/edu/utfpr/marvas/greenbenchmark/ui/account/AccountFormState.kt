package br.edu.utfpr.marvas.greenbenchmark.ui.account

/**
 * Data validation state of the account form.
 */
data class AccountFormState(
    val firstNameError: Int? = null,
    val emailError: Int? = null,
    val phoneNumberError: Int? = null,
    val phoneCountryCodeError: Int? = null,
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)