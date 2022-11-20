package br.edu.utfpr.marvas.greenbenchmark.data.model

import br.edu.utfpr.marvas.greenbenchmark.commons.Constants

data class Config(
    val testLoad: Long = Constants.DEFAULT_LOAD,
    val mediaUri: String,
    val uploadUri: String
)
