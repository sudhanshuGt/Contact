package dev.sudhanshu.contactform.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import dev.sudhanshu.contactform.data.model.FormData
import dev.sudhanshu.contactform.ui.screen.ui.theme.ContactFormTheme
import dev.sudhanshu.contactform.viewmodel.ContactFormViewModel




class AllContactFormsScreen : ComponentActivity() {
    private val viewModel: ContactFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactFormTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactFormsScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Load forms when the activity is created
        viewModel.loadForm()
    }
}

@Composable
fun ContactFormsScreen(viewModel: ContactFormViewModel, modifier: Modifier = Modifier) {
    val formList by viewModel.formList.observeAsState(emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "All Forms",
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )

        if (formList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No forms found.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(formList) { formData ->
                    FormItem(formData)
                }
            }
        }
    }
}

@Composable
fun FormItem(formData: FormData) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Age: ${formData.age}")
            Text(text = "Selfie Path: ${formData.selfiePath}")
            Text(text = "Recording Path: ${formData.recordingPath}")
            Text(text = "Submitted On: ${formData.submitTime}")
        }
    }
}


