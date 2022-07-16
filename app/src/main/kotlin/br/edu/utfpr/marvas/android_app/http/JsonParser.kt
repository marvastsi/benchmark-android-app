package br.edu.utfpr.marvas.android_app.http

import android.util.JsonReader
import android.util.JsonWriter
import br.edu.utfpr.marvas.android_app.model.Message
import br.edu.utfpr.marvas.android_app.model.User
import java.io.*

object JsonParser {
    @Throws(IOException::class)
    fun readJsonStream(ins: InputStream?): Message {
        val reader = JsonReader(InputStreamReader(ins, "UTF-8"))
        return reader.use {
            readMessage(it)
        }
    }

    @Throws(IOException::class)
    fun readMessage(reader: JsonReader): Message {
        var id: Long = -1
        var text = ""
        var user: User? = null
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "id" -> {
                    id = reader.nextLong()
                }
                "text" -> {
                    text = reader.nextString()
                }
                "user" -> {
                    user = readUser(reader)
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()
        return Message(id, text, user!!)
    }

    @Throws(IOException::class)
    fun readUser(reader: JsonReader): User {
        var username = ""
        var id = -1L
        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "id" -> {
                    id = reader.nextLong()
                }
                "name" -> {
                    username = reader.nextString()
                }
                else -> {
                    reader.skipValue()
                }
            }
        }
        reader.endObject()
        return User(id, username)
    }

    @Throws(IOException::class)
    fun writeJsonStream(out: OutputStream?, message: Message) {
        val writer = JsonWriter(OutputStreamWriter(out, "UTF-8"))
        writer.use {
            it.setIndent("  ")
            writeMessage(writer, message)
            it.flush()
        }
    }

    @Throws(IOException::class)
    fun writeMessage(writer: JsonWriter, message: Message) {
        writer.beginObject()
        writer.name("id").value(message.id)
        writer.name("text").value(message.text)
        writer.name("user")
        writeUser(writer, message.user)
        writer.endObject()
    }

    @Throws(IOException::class)
    fun writeUser(writer: JsonWriter, user: User) {
        writer.beginObject()
        writer.name("id").value(user.id)
        writer.name("name").value(user.name)
        writer.endObject()
    }
}