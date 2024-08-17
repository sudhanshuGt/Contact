package dev.sudhanshu.contactform.viewmodel



import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.sudhanshu.contactform.data.model.FormData
import dev.sudhanshu.contactform.data.repository.ContactFormRepository
import dev.sudhanshu.contactform.util.AudioRecorder
import dev.sudhanshu.contactform.util.GeotaggingUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class ContactFormViewModel : ViewModel() {
    val age = mutableStateOf("")
    val selfieUri = mutableStateOf<Uri?>(null)
    private val recordingPath = mutableStateOf<String?>(null)
    private val submissionTime = mutableStateOf("")

    private val repository = ContactFormRepository() // Assuming a default constructor
    private val audioRecorder = AudioRecorder()

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted

    fun updatePermissionState(granted: Boolean) {
        _permissionsGranted.update { granted }
    }

    fun startAudioRecording() {
        viewModelScope.launch {
            audioRecorder.startRecording()
        }
    }

    fun stopAudioRecording() {
        viewModelScope.launch {
            recordingPath.value = audioRecorder.stopRecording()
        }
    }

    private val _formList = MutableLiveData<List<FormData>>()
    val formList: LiveData<List<FormData>> = _formList


    fun loadForm(){
        viewModelScope.launch {
            _formList.value = repository.getAllForms()
        }
    }


    fun submitForm() {
        viewModelScope.launch {
            val geoTag = GeotaggingUtil.getGeolocation()
            val timestamp = SimpleDateFormat("dd_MM_yyyy_HH_mm_a", Locale.getDefault()).format(Date())
            submissionTime.value = timestamp

            val formData = FormData(
                age = age.value.toInt(),
                selfiePath = selfieUri.value.toString(),
                recordingPath = recordingPath.value ?: "",
                submitTime = timestamp
            )

            repository.saveFormData(formData)
        }
    }
}




