package br.edu.utfpr.marvas.greenbenchmark.data.model

import java.util.*

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class Account(
    val accountId: String = UUID.randomUUID().toString(),
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val phoneCountryCode: String,
    val active: Boolean,
    val notification: Boolean,
    val username: String,
    val password: String
)