package br.edu.utfpr.marvas.greenbenchmark.data.model

data class Account(
    val id: String? = null,
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