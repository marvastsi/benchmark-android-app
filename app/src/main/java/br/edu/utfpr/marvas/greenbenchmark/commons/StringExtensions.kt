package br.edu.utfpr.marvas.greenbenchmark.commons

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
fun String.toLocalDate():LocalDate = LocalDate.parse(this)

fun String.capitalize() = this.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(
        Locale.ROOT
    ) else it.toString()
}