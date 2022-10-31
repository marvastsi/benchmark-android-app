package br.edu.utfpr.marvas.greenbenchmark.http

class HttpException(
    val code: Int,
    override val message: String
) : Exception(message)