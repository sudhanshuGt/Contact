package dev.sudhanshu.contactform.ui.screen



import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sudhanshu.contactform.data.model.FormData

@Composable
fun SummaryScreen(formDataList: List<FormData>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(formDataList) { formData ->
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(text = "Age: ${formData.age}")
                Text(text = "Selfie: ${formData.selfiePath}")
                Text(text = "Recording: ${formData.recordingPath}")
                Text(text = "Submission: ${formData.submitTime}")
            }
        }
    }
}
