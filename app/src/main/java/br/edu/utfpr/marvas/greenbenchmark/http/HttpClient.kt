package br.edu.utfpr.marvas.greenbenchmark.http

import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class HttpClient {
    fun <T> get(
        url: String,
        responseClass: Class<T>,
        headers: Array<Pair<String, String>> = emptyArray()
    ): HttpResponse<T> {
        return runBlocking {
            val urlConnection = URL(url)
            with(urlConnection.openConnection() as HttpURLConnection) {
                headers.forEach {
                    setRequestProperty(it.first, it.second)
                }
                requestMethod = HttpMethods.GET.name

                if (isSuccess(responseCode)) {
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val responseBody = Gson().fromJson(
                        inputStreamReader,
                        responseClass
                    )
                    HttpResponse(
                        status = responseCode,
                        message = responseMessage,
                        body = responseBody
                    )
                } else {
                    throw HttpException(responseCode, responseMessage)
                }
            }
        }
    }

    fun <T> post(
        url: String,
        body: Any,
        responseClass: Class<T>,
        headers: Array<Pair<String, String>> = emptyArray()
    ): HttpResponse<T> {
        return runBlocking {
            val urlConnection = URL(url)
            with(urlConnection.openConnection() as HttpURLConnection) {
                headers.forEach {
                    setRequestProperty(it.first, it.second)
                }
                requestMethod = HttpMethods.POST.name
                doOutput = true
                doInput = true

                val outputStreamWriter = OutputStreamWriter(outputStream, "UTF-8")
                Gson().toJson(
                    body,
                    outputStreamWriter
                )
                outputStreamWriter.flush()

                if (isSuccess(responseCode)) {
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val responseBody = Gson().fromJson(
                        inputStreamReader,
                        responseClass
                    )
                    HttpResponse(
                        status = responseCode,
                        message = responseMessage,
                        body = responseBody
                    )
                } else {
                    throw HttpException(responseCode, responseMessage)
                }
            }
        }
    }

    fun <T> put(
        url: String,
        body: Any,
        responseClass: Class<T>,
        headers: Array<Pair<String, String>> = emptyArray()
    ): HttpResponse<T> {
        return runBlocking {
            val urlConnection = URL(url)
            with(urlConnection.openConnection() as HttpURLConnection) {
                headers.forEach {
                    setRequestProperty(it.first, it.second)
                }
                requestMethod = HttpMethods.PUT.name
                doInput = true
                doOutput = true

                val outputStreamWriter = OutputStreamWriter(outputStream, "UTF-8")
                Gson().toJson(
                    body,
                    outputStreamWriter
                )
                outputStreamWriter.flush()

//                val typeOfSrc: Type = object : TypeToken<Collection<T?>?>() {}.type
                if (isSuccess(responseCode)) {
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val responseBody = Gson().fromJson(
                        inputStreamReader,
                        responseClass
                    )
                    HttpResponse(
                        status = responseCode,
                        message = responseMessage,
                        body = responseBody
                    )
                } else {
                    throw HttpException(responseCode, responseMessage)
                }
            }
        }
    }

    fun getFile(
        url: String,
        filePath: String,
        headers: Array<Pair<String, String>> = emptyArray()
    ): HttpResponse<File> {
        return runBlocking {
            val urlConnection = URL(url)
            with(urlConnection.openConnection() as HttpURLConnection) {
                headers.forEach {
                    setRequestProperty(it.first, it.second)
                }
                requestMethod = HttpMethods.GET.name

                if (isSuccess(responseCode)) {
                    val file = File(filePath)
                    FileOutputStream(file).use { fileOutputStream ->
                        InputStreamReader(inputStream).use { inputStreamReader ->
                            var byteRead: Int
                            do {
                                byteRead = inputStreamReader.read()
                                if (byteRead != -1) {
                                    fileOutputStream.write(byteRead)
                                }
                            } while (byteRead != -1)
                        }
                    }

                    HttpResponse(
                        status = responseCode,
                        message = responseMessage,
                        body = file
                    )
                } else {
                    throw HttpException(responseCode, responseMessage)
                }
            }
        }
    }

    fun <T> postFile(
        url: String,
        filePath: String,
        responseClass: Class<T>,
        headers: Array<Pair<String, String>> = emptyArray()
    ): HttpResponse<T> {
        return runBlocking {
            val urlConnection = URL(url)
            with(urlConnection.openConnection() as HttpURLConnection) {
                headers.forEach {
                    setRequestProperty(it.first, it.second)
                }
                requestMethod = HttpMethods.POST.name
                doOutput = true
                doInput = true

                OutputStreamWriter(outputStream).use { outputStreamWriter ->
                    FileInputStream(filePath).use { fileInputStream ->
                        var byteRead: Int
                        do {
                            byteRead = fileInputStream.read()
                            if (byteRead != -1) {
                                outputStreamWriter.write(byteRead)
                            }
                        } while (byteRead != -1)
                    }
                    outputStreamWriter.flush()
                }

                if (isSuccess(responseCode)) {
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val responseBody = Gson().fromJson(
                        inputStreamReader,
                        responseClass
                    )
                    HttpResponse(
                        status = responseCode,
                        message = responseMessage,
                        body = responseBody
                    )
                } else {
                    throw HttpException(responseCode, responseMessage)
                }
            }
        }
    }

    companion object {
        fun createDefaultHeader(
            authorization: String = "",
            accept: String = "application/json",
            contentType: String = "application/json"
        ) = arrayOf(
            "Accept" to accept,
            "Content-Type" to contentType,
            "Authorization" to authorization
        )

        fun isSuccess(status: Int) = status in 200..207
    }
}

enum class HttpMethods {
    GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
}
