package br.edu.utfpr.marvas.greenbenchmark.data.model

import br.edu.utfpr.marvas.greenbenchmark.commons.Constants

data class Config(
    val testLoad: Int = Constants.DEFAULT_LOAD,
    val mediaUri: String,
    val uploadUri: String,
    val downloadUri: String,
    val serverUrl: String,
    val specificScenario: Int
)
