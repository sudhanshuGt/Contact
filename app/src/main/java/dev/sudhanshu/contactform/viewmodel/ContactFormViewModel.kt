package dev.sudhanshu.contactform.viewmodel



import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.sudhanshu.contactform.data.model.FormData
import dev.sudhanshu.contactform.data.repository.ContactFormRepository
import dev.sudhanshu.contactform.util.ActivityContextHolder
import dev.sudhanshu.contactform.util.AudioRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location: StateFlow<Pair<Double, Double>?> = _location.asStateFlow()

    fun updateLocation(latitude: Double, longitude: Double) {
        _location.value = Pair(latitude, longitude)
    }


    private val _fileNames = MutableLiveData<List<String>>()
    val fileNames: LiveData<List<String>> get() = _fileNames



    fun loadFileNames() {
        viewModelScope.launch(Dispatchers.IO) {
            val names = repository.getAllJsonFileNames()
            _fileNames.postValue(names)
        }
    }


    fun submitForm() {
        viewModelScope.launch {
             val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            submissionTime.value = timestamp
            val formData = FormData(
                age = age.value.toInt(),
                selfiePath = selfieUri.value.toString(),
                recordingPath = recordingPath.value ?: "",
                submitTime = timestamp
            )
            selfieUri.value = null
            recordingPath.value = null
            repository.saveFormData(formData)
        }
    }
}




