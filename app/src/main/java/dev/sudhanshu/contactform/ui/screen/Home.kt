package dev.sudhanshu.contactform.ui.screen

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.sudhanshu.contactform.ui.theme.ContactFormTheme
import dev.sudhanshu.contactform.viewmodel.ContactFormViewModel


import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import dev.sudhanshu.contactform.util.ActivityContextHolder


class Home : ComponentActivity() {


    private lateinit var contactFormViewModel: ContactFormViewModel

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityContextHolder.setActivityContext(this)
        contactFormViewModel = ViewModelProvider(this)[ContactFormViewModel::class.java]

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionsGranted = permissions.values.all { it }
            contactFormViewModel.updatePermissionState(allPermissionsGranted)
        }

        setContent {
            ContactFormTheme {
                Scaffold ( topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Contact Form",
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.White
                            )
                        },
                        actions = {
                            IconButton(onClick = {
                                val intent = Intent(this@Home, AllContactFormsScreen::class.java)
                                startActivity(intent)
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "More Options", tint = Color.White)
                            }
                        },
                    )
                }){ padding ->
                    padding.calculateTopPadding()
                    PermissionHandler(
                        viewModel = contactFormViewModel,
                        permissionLauncher = permissionLauncher
                    )

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityContextHolder.clearActivityContext()
    }
}

@Composable
fun PermissionHandler(
    viewModel: ContactFormViewModel,
    permissionLauncher: ActivityResultLauncher<Array<String>>
) {
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()

    LaunchedEffect(Unit) {
        checkAndRequestPermissions(permissionLauncher)
    }

    if (permissionsGranted) {
        ContactFormScreen(viewModel)
    } else {
        PermissionRequestUI(permissionLauncher)
    }
}

private fun checkAndRequestPermissions(
    permissionLauncher: ActivityResultLauncher<Array<String>>
) {
    val permissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    permissionLauncher.launch(permissions)
}

@Composable
fun PermissionRequestUI(permissionLauncher: ActivityResultLauncher<Array<String>>) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
    }


}






