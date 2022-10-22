package br.edu.utfpr.marvas.greenbenchmark.http

data class HttpResponse<T>(
    val status: Int,
    val message: String,
    val body: T? = null
)