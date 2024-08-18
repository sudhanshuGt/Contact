package dev.sudhanshu.contactform.ui.screen


import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import dev.sudhanshu.contactform.R
import dev.sudhanshu.contactform.location.LocationManager
import dev.sudhanshu.contactform.location.NoPermissionException
import dev.sudhanshu.contactform.viewmodel.ContactFormViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactFormScreen(viewModel: ContactFormViewModel, locationManager: LocationManager) {
    val context = LocalContext.current
    val selfieUri by remember { viewModel.selfieUri }
    val age by remember { viewModel.age }
    var isTyping by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var recordingJob: Job? by remember { mutableStateOf(null) }
    var isRecording by remember { mutableStateOf(false) }



    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selfieUri?.let { uri ->
                try {
                    coroutineScope.launch {
                        locationManager.listenToLocation().collect { loc ->
                            processAndSaveImage(context, uri, loc.latitude, loc.longitude, callback = {
                                viewModel.selfieUri.value = uri
                            })
                        }
                    }
                } catch (ex : Exception) {
                    Log.i("--LocationUpdate--", ex.printStackTrace().toString())
                }


            }
        }
    }

    fun startRecording() {
        if (!isRecording) {
            isRecording = true
            recordingJob = coroutineScope.launch(Dispatchers.IO) {
                viewModel.startAudioRecording()
            }
        }
    }

    fun validateInput(): Boolean {
        val ageValue = age.toIntOrNull()
        return when {
            ageValue == null || ageValue <= 0 || ageValue > 100 -> {
                context.showToast("Please enter a valid age between 1 and 100.")
                false
            }
            selfieUri == null -> {
                context.showToast("Please take a selfie.")
                false
            }
            else -> true
        }
    }

    fun clearInputFields() {
        viewModel.age.value = ""
        viewModel.selfieUri.value = null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = age,
                onValueChange = {
                    viewModel.age.value = it
                    isTyping = it.isNotEmpty()
                    if (isTyping) {
                        startRecording()
                    }
                },
                label = { Text("Enter your age") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    cameraPermissionState.launchPermissionRequest()
                    val uri = createImageUri(context)
                    viewModel.selfieUri.value = uri
                    launcher.launch(uri)
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
            ) {
                Icon(imageVector = Icons.Default.Face, contentDescription = "Camera Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take a Selfie", color = Color.White)
            }

            Button(
                onClick = {
                    if (validateInput()) {
                        viewModel.stopAudioRecording()
                        viewModel.submitForm()

                        clearInputFields()  // Clear input fields after submission

                        val intent = Intent(context, ContactSummary::class.java)
                        context.startActivity(intent)
                    }
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF03DAC5))
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Submit Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit", color = Color.White)
            }
        }
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun createImageUri(context: Context): Uri {
    val contentResolver = context.contentResolver
    val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val imageDetails = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "selfie_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return contentResolver.insert(imageCollection, imageDetails)!!
}


@Composable
private fun uriToCaptureImage(): Uri {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return LocalContext.current.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
}

private fun processAndSaveImage(
    context: Context,
    imageUri: Uri,
    latitude: Double,
    longitude: Double,
    callback: (Uri?) -> Unit
) {
    try {
        // Load the bitmap from URI
        val bitmap = context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: throw IllegalArgumentException("Failed to load bitmap from $imageUri")

        // Create a mutable bitmap to draw on
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = android.graphics.Canvas(mutableBitmap)
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLUE

        }
        val text = "Lat: $latitude, Long: $longitude"
        canvas.drawText(text, 50f, 50f, paint)

        // Save the bitmap back to the URI
        context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
            mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            callback(imageUri)
        } ?: throw IllegalArgumentException("Failed to open output stream for $imageUri")
    } catch (e: Exception) {
        e.printStackTrace()
        callback(null) // Handle the error case
    }
}





