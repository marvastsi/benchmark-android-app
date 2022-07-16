package br.edu.utfpr.marvas.android_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.edu.utfpr.marvas.android_app.async.executeAsyncTask
import br.edu.utfpr.marvas.android_app.http.HttpClient
import br.edu.utfpr.marvas.android_app.model.Message

@Composable
fun MainView() {
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val (index, setIndex) = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    var message: Message? = null
    val defaultModifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()
    val client = HttpClient()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(modifier = defaultModifier) {
            BenchmarkButton("USO DE API") {
                setIndex(1)
                coroutineScope.executeAsyncTask(
                    doInBackground = {
                        client.executeGetMessage().body as Message
                    },
                    onPostExecute = {
                        message = it
                    },
                    onPreExecute = {}
                )
                setShowDialog(true)
            }
        }
        Row(modifier = defaultModifier) {
            BenchmarkButton("FORMULÁRIO") {
                setIndex(2)
                setShowDialog(true)
            }
        }
        Row(modifier = defaultModifier) {
            BenchmarkButton("ENVIAR E RECEBER ARQUIVOS") {
                setIndex(3)
                setShowDialog(true)
            }
        }
        Row(modifier = defaultModifier) {
            BenchmarkButton(text = "EXECUÇÃO DE MÍDIA") {
                setIndex(4)
                setShowDialog(true)
            }
        }
        ToDoAlertDialog(message, index, showDialog, setShowDialog)
    }
}

@Composable
fun ToDoAlertDialog(
    message: Message?,
    index: Int,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit
) {
    if (showDialog) {
        AlertDialog(
            title = { Text(text = "Ação não implementada") },
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { setShowDialog(false) })
                { Text(text = "OK") }
            },
            dismissButton = {
                TextButton(onClick = { setShowDialog(false) })
                { Text(text = "Cancelar") }
            },
            text = {
                Text(text = "Funcionalidade [$index] não implementada!\n$message")
            }
        )
    }
}

@Composable
fun BenchmarkButton(text: String, action: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { action() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Text(text)
    }
}