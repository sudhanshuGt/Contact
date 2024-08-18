package dev.sudhanshu.contactform.ui.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import dev.sudhanshu.contactform.R
import dev.sudhanshu.contactform.data.model.FormData
import dev.sudhanshu.contactform.ui.screen.ui.theme.ContactFormTheme
import dev.sudhanshu.contactform.viewmodel.ContactFormViewModel




class AllContactFormsScreen : ComponentActivity() {
    private val viewModel: ContactFormViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadFileNames()

        setContent {
            ContactFormTheme {
                Scaffold(
                    topBar = {
                        androidx.compose.material3.TopAppBar(
                            title = {
                                Text(
                                    text = "All Contact Forms",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color.White
                                )
                            },
                        )
                    }
                ) { innerPadding ->
                    FileNamesScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun FileNamesScreen(viewModel: ContactFormViewModel, modifier: Modifier = Modifier) {
    val fileNames by viewModel.fileNames.observeAsState(emptyList())
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (fileNames.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No forms found.")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(fileNames) { fileName ->
                    FileNameItem(fileName, onClick = {
                        val intent = Intent(context, ContactSummary::class.java).apply {
                            putExtra("fileName", fileName)
                        }
                        context.startActivity(intent)
                    })
                }
            }
        }
    }
}

@Composable
fun FileNameItem(fileName: String, onClick: () -> Unit) {
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painterResource(R.drawable.file),
                contentDescription = "File Icon",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = fileName,
                fontSize = 16.sp,
                color = Color.Black,
            )
        }
    }
}





