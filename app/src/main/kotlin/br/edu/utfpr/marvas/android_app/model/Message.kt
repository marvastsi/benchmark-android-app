package br.edu.utfpr.marvas.android_app.model

data class Message(
    val id: Long,
    val text: String,
    val user: User
)
