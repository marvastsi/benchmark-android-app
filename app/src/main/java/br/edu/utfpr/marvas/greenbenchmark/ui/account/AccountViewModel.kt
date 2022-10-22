package br.edu.utfpr.marvas.greenbenchmark.ui.account

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.edu.utfpr.marvas.greenbenchmark.R
import br.edu.utfpr.marvas.greenbenchmark.data.AccountRepository
import br.edu.utfpr.marvas.greenbenchmark.data.Result
import br.edu.utfpr.marvas.greenbenchmark.data.model.Account

class AccountViewModel(private val accountRepository: AccountRepository) : ViewModel() {

    private val _accountForm = MutableLiveData<AccountFormState>()
    val accountFormState: LiveData<AccountFormState> = _accountForm

    private val _createAccountResult = MutableLiveData<CreateAccountResult>()
    val accountResult: LiveData<CreateAccountResult> = _createAccountResult

    fun save(account: Account) {
        // can be launched in a separate asynchronous job
        val result = accountRepository.save(account)

        if (result is Result.Success) {
            _createAccountResult.postValue(
                CreateAccountResult(success = AccountCreatedView.fromAccount(result.data))
            )
        } else {
            _createAccountResult.postValue(CreateAccountResult(error = R.string.login_failed))
        }
    }

    fun accountDataChanged(
        firstName: String,
        email: String,
        phoneNumber: String,
        phoneCountryCode: String,
        username: String,
        password: String
    ) {
        if (firstName.isBlank()) {
            _accountForm.value = AccountFormState(firstNameError = R.string.invalid_username)
        } else if (!isEmailValid(email)) {
            _accountForm.value = AccountFormState(emailError = R.string.invalid_password)
        } else if (!isPhoneNumberValid(phoneNumber)) {
            _accountForm.value = AccountFormState(phoneNumberError = R.string.invalid_password)
        } else if (!isPhoneCountryCodeValid(phoneCountryCode)) {
            _accountForm.value = AccountFormState(phoneCountryCodeError = R.string.invalid_password)
        } else if (!isUserNameValid(username)) {
            _accountForm.value = AccountFormState(usernameError = R.string.invalid_password)
        } else if (!isPasswordValid(password)) {
            _accountForm.value = AccountFormState(passwordError = R.string.invalid_password)
        } else {
            _accountForm.value = AccountFormState(isDataValid = true)
        }
    }

    private fun isEmailValid(username: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(username).matches()
    }

    private fun isPhoneNumberValid(username: String): Boolean {
        return username.isNotBlank() && Patterns.PHONE.matcher(username).matches()
    }

    private fun isPhoneCountryCodeValid(phoneCountryCode: String): Boolean {
        return phoneCountryCode.startsWith("+") && phoneCountryCode.contains("55")
    }

    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            isEmailValid(username)
        } else {
            username.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}