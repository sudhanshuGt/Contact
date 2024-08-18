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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import dev.sudhanshu.contactform.location.LocationManager
import dev.sudhanshu.contactform.location.LocationManagerImpl
import dev.sudhanshu.contactform.util.ActivityContextHolder


class Home : ComponentActivity() {


    private lateinit var contactFormViewModel: ContactFormViewModel

    val locationManager: LocationManager by lazy {
        LocationManagerImpl(this)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        ActivityContextHolder.setActivityContext(this)
        contactFormViewModel = ViewModelProvider(this)[ContactFormViewModel::class.java]
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true // Set light or dark status bar icons
        window.statusBarColor = Color.Blue.toArgb()

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionsGranted = permissions.values.all { it }
            contactFormViewModel.updatePermissionState(allPermissionsGranted)
        }

        setContent {
            ContactFormTheme {
                Scaffold (
                    modifier = Modifier.background(Color.White),
                    topBar = {
                    androidx.compose.material.TopAppBar(
                        backgroundColor = MaterialTheme.colors.primary,
                        elevation = 4.dp,
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
                        permissionLauncher = permissionLauncher,
                        locationManager
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
    permissionLauncher: ActivityResultLauncher<Array<String>>,
    locationManager: LocationManager
) {
    val permissionsGranted by viewModel.permissionsGranted.collectAsState()

    LaunchedEffect(Unit) {
        checkAndRequestPermissions(permissionLauncher)
    }

    if (permissionsGranted) {
        ContactFormScreen(viewModel, locationManager)
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






