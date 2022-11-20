package br.edu.utfpr.marvas.greenbenchmark.http

import android.content.ContentResolver
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder

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
                    val responseBody = Gson().fromJson(inputStreamReader, responseClass)

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
                Gson().toJson(body, outputStreamWriter)
                outputStreamWriter.flush()

                if (isSuccess(responseCode)) {
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val responseBody = Gson().fromJson(inputStreamReader, responseClass)

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
                Gson().toJson(body, outputStreamWriter)
                outputStreamWriter.flush()

                if (isSuccess(responseCode)) {
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val responseBody = Gson().fromJson(inputStreamReader, responseClass)

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
        fileName: String,
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
                    val file = readRemoteFile(fileName, inputStream)

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
        encodedPath: String,
        contentResolver: ContentResolver,
        responseClass: Class<T>,
        headers: Array<Pair<String, String>> = emptyArray()
    ): HttpResponse<T> {
        return runBlocking {
            val fileName = getFileName(encodedPath)
            val urlConnection = URL(url)
            with(urlConnection.openConnection() as HttpURLConnection) {
                headers.forEach {
                    setRequestProperty(it.first, it.second)
                }
                setRequestProperty("Content-Type", "multipart/form-data;boundary=$BOUNDARY")
                setRequestProperty("file", fileName)

                requestMethod = HttpMethods.POST.name
                doOutput = true
                doInput = true
                useCaches = false

                writeRemoteFile(contentResolver, encodedPath.toUri(), fileName, outputStream)

                if (isSuccess(responseCode)) {
                    val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                    val responseBody = Gson().fromJson(inputStreamReader, responseClass)

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

    private fun getFileName(encodedPath: String): String {
        val decoded = URLDecoder.decode(encodedPath, "UTF-8")
        return decoded.toUri().lastPathSegment!!
    }

    private fun readRemoteFile(fileName: String, inputStream: InputStream): File {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (file.exists()) {
            file.delete().also {
                file.createNewFile()
            }
        }
        FileOutputStream(file).use { fileOutputStream ->
            DataInputStream(inputStream).use { dataInputStream ->
                var bytesAvailable = dataInputStream.available()
                var bufferSize = bytesAvailable.coerceAtMost(MAX_BUFFER_SIZE)
                val buffer = ByteArray(bufferSize)
                var bytesRead = dataInputStream.read(buffer, 0, bufferSize)
                while (bytesRead > 0) {
                    fileOutputStream.write(buffer, 0, bufferSize)
                    bytesAvailable = dataInputStream.available()
                    bufferSize = bytesAvailable.coerceAtMost(MAX_BUFFER_SIZE)
                    bytesRead = dataInputStream.read(buffer, 0, bufferSize)
                }
            }
            fileOutputStream.flush()
        }

        return file
    }

    private fun writeRemoteFile(
        contentResolver: ContentResolver,
        fileUri: Uri,
        fileName: String,
        outputStream: OutputStream
    ) {
        DataOutputStream(outputStream).use { dataOutputStream ->
            dataOutputStream.writeBytes("$TWO_HYPHENS$BOUNDARY$LINE_END")
            dataOutputStream.writeBytes(
                "Content-Disposition: form-data; name=\"file\";filename=\"$fileName\"$LINE_END"
            )
            dataOutputStream.writeBytes(LINE_END)
            contentResolver.openInputStream(fileUri).use { fileInputStream ->
                var bytesAvailable = fileInputStream!!.available()
                var bufferSize = bytesAvailable.coerceAtMost(MAX_BUFFER_SIZE)
                val buffer = ByteArray(bufferSize)
                var bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                while (bytesRead > 0) {
                    dataOutputStream.write(buffer, 0, bufferSize)
                    bytesAvailable = fileInputStream.available()
                    bufferSize = bytesAvailable.coerceAtMost(MAX_BUFFER_SIZE)
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize)
                }
                dataOutputStream.writeBytes(LINE_END)
                dataOutputStream.writeBytes("$TWO_HYPHENS$BOUNDARY$TWO_HYPHENS$LINE_END")
            }
            dataOutputStream.flush()
        }
    }

    companion object {
        const val LINE_END = "\r\n"
        const val TWO_HYPHENS = "--"
        const val BOUNDARY = "*****"
        const val MAX_BUFFER_SIZE = (1 * 1024 * 1024)
        val ENCTYPE = "ENCTYPE" to "multipart/form-data"

        fun createDefaultHeader(
            authorization: String = "",
            accept: String = "application/json",
            contentType: String = "application/json",
        ) = arrayOf(
            "Accept" to accept,
            "Content-Type" to contentType,
            "Authorization" to authorization,
            "Connection" to "Keep-Alive"
        )

        fun isSuccess(status: Int) = status in 200..207
    }
}

enum class HttpMethods {
    GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE
}
