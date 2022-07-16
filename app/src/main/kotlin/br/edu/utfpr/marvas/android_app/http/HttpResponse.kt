package br.edu.utfpr.marvas.android_app.http

data class HttpResponse<T>(
    val status: Int,
    val message: String,
    val body: T? = null
)