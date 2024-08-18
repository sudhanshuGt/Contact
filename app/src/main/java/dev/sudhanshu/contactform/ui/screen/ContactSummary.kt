package dev.sudhanshu.contactform.ui.screen

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.sudhanshu.contactform.ui.screen.ui.theme.ContactFormTheme
import dev.sudhanshu.contactform.util.FileHolder
import kotlinx.coroutines.delay
import java.io.File



class ContactSummary : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true // Set light or dark status bar icons
        window.statusBarColor = Color.Blue.toArgb()

        val fileName = intent.getStringExtra("fileName") ?: FileHolder.getFileName()
        if (fileName != null) {
            Log.i("--ContactFile--", fileName)
        }

        setContent {
            ContactFormTheme {
                Scaffold(
                    modifier = Modifier.background(Color.White),
                    topBar = {
                        TopAppBar(
                            modifier = Modifier.background(color = Color.Blue),
                            title = {
                                androidx.compose.material3.Text(
                                    text = "Contact Summary",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.White
                                )
                            },
                        )
                    }
                ) { padding ->
                    padding.calculateTopPadding()
                    if (fileName != null) {
                        JsonTableScreen(fileName)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        FileHolder.clearFile();
    }
}
fun parseJsonFile(filePath: String): Map<String, Any>? {
    return try {
        val file = File(filePath)
        Log.i("--FilePath--", "Reading file from: $filePath")
        if (!file.exists()) {
            Log.e("--FileError--", "File does not exist at path: $filePath")
            return null
        }

        val jsonContent = file.readText()
        Log.i("--JsonContent--", jsonContent)

        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        gson.fromJson(jsonContent, type)
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("--JsonError--", "Error parsing JSON: ${e.message}")
        null
    }
}

@Composable
fun JsonTableScreen(fileName: String) {
    val filePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}/$fileName"
    val jsonData by remember { mutableStateOf(parseJsonFile(filePath)) }
    var imageDialogPath by remember { mutableStateOf<String?>(null) }
    var audioDialogPath by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    androidx.compose.material3.Text(
                        text = "Contact Summary",
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
                elevation = 4.dp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            jsonData?.forEach { (key, value) ->
                val displayText = when (value) {
                    is Number -> if (value.toDouble() == value.toInt().toDouble()) value.toInt().toString() else value.toString()
                    is String -> when {
                        value.startsWith("content://") -> value.substringAfterLast("/") // Handle content URI for Q2
                        value.startsWith("/data/") -> value.substringAfterLast("/") // Handle file path for recording
                        else -> value
                    }
                    else -> value.toString()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White)
                        .border(1.dp, Color.Gray),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        color = Color.Blue,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                    )

                    if (value is String) {
                        when {
                            value.startsWith("content://") -> {
                                Text(
                                    text = displayText,
                                    color = Color.Green,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                        .underline()
                                        .clickable {
                                            imageDialogPath = value
                                        }
                                )
                            }
                            value.startsWith("/data/") -> {
                                Text(
                                    text = displayText,
                                    color = Color.Green,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                        .underline()

                                )
                            }
                            else -> {
                                Text(
                                    text = displayText,
                                    color = Color.Black,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = displayText,
                            color = Color.Black,
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        )
                    }
                }
            } ?: run {
                Text(text = "No data available.")
            }
        }

        // Showing Image Dialog
        imageDialogPath?.let {
            ImageDialog(imagePath = it, onDismiss = { imageDialogPath = null })
        }


    }
}

@Composable
fun Modifier.underline(): Modifier = this.then(Modifier.padding(bottom = 1.dp).background(Color.Transparent))

@Composable
fun ImageDialog(imagePath: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Image Preview") },
        text = {
            Image(
                painter = rememberImagePainter(data = imagePath),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}


