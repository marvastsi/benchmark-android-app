package br.edu.utfpr.marvas.android_app.http

import android.util.Log
import br.edu.utfpr.marvas.android_app.model.Message
import kotlinx.coroutines.runBlocking
import java.net.HttpURLConnection
import java.net.URL

class HttpClient(
    private val baseUrl: String = "http://127.0.0.1:3000/api"
) {
    fun executeGetMessage(
        url: String = "/message/find"
    ): HttpResponse<Message> {
        return runBlocking {
            Log.d("HTTP_CLIENT", "executeGetMessage: ${baseUrl + url}")
            val urlConnection = URL(baseUrl + url)
            with(urlConnection.openConnection() as HttpURLConnection) {
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Content-Type", "application/json")
                requestMethod = HttpMethods.GET.name

                return@runBlocking if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseBody = JsonParser.readJsonStream(inputStream)
                    HttpResponse(
                        responseCode,
                        responseMessage,
                        responseBody
                    )
                } else
                    HttpResponse(
                        responseCode,
                        responseMessage
                    )
            }
        }
    }

    fun executePostMessage(
        url: String = "/message/save",
        body: Message
    ): HttpResponse<out String> {
        return runBlocking {
            val urlConnection = URL(baseUrl.plus(url))
            with(urlConnection.openConnection() as HttpURLConnection) {
                setRequestProperty("Accept", "application/json")
                setRequestProperty("Content-Type", "application/json")

                requestMethod = HttpMethods.POST.name
                doOutput = true
                JsonParser.writeJsonStream(outputStream, body)

                return@runBlocking HttpResponse(
                    responseCode,
                    responseMessage
                )
            }
        }
    }
}

enum class HttpMethods {
    GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
}

