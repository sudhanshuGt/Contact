package dev.sudhanshu.contactform.ui.screen


import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dev.sudhanshu.contactform.R
import dev.sudhanshu.contactform.viewmodel.ContactFormViewModel






@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactFormScreen(viewModel: ContactFormViewModel) {
    val context = LocalContext.current
    val selfieUri by remember { viewModel.selfieUri }
    val age by remember { viewModel.age }
    var isTyping by remember { mutableStateOf(false) }

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val micPermissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selfieUri?.let { viewModel.selfieUri.value = it }
        }
    }

    LaunchedEffect(isTyping) {
        if (isTyping) {
            viewModel.startAudioRecording()
        } else {
            viewModel.stopAudioRecording()
        }
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
                },
                label = { Text("Enter your age") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
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
                    viewModel.stopAudioRecording()
                    viewModel.submitForm()
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF03DAC5))
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Camera Icon")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit", color = Color.White)
            }
        }
    }
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



