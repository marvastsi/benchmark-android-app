package br.edu.utfpr.marvas.greenbenchmark.data

import br.edu.utfpr.marvas.greenbenchmark.data.model.Account
import java.io.IOException

/**
 * Class that handles account save and retrieves.
 */
class AccountDataSource {

    fun save(account: Account): Result<Account> {
        return try {
            // TODO: handle account save
            Result.Success(account)
        } catch (e: Throwable) {
            Result.Error(IOException("Error saving account ", e))
        }
    }

    fun find(accountId: String): Result<Account> {
        val fakeUser = Account(
            accountId = accountId,
            firstName = "Jane",
            lastName = "Doe",
            email = "jane.doe@gmail.com",
            phoneNumber = "11900880088",
            phoneCountryCode = "+55",
            active = false,
            notification = true,
            username = "jane.doe",
            password = "qwert123"
        )
        return Result.Success(fakeUser)
    }
}