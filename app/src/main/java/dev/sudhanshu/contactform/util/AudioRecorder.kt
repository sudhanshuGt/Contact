package dev.sudhanshu.contactform.util



import android.content.Context
import android.media.MediaRecorder
import java.io.File


class AudioRecorder() {
    private var recorder: MediaRecorder? = null
    private var filePath: String? = null

    fun startRecording() {
        val fileName = "recording_${System.currentTimeMillis()}.aac"
        val file = File(ActivityContextHolder.getActivityContext()!!.filesDir, fileName)
        filePath = file.absolutePath

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(filePath)
            prepare()
            start()
        }
    }

    fun stopRecording(): String? {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        val wavFilePath = convertToWav(filePath)
        return wavFilePath
    }

    private fun convertToWav(aacFilePath: String?): String? {
        if (aacFilePath == null) return null
        val wavFileName = aacFilePath.replace(".aac", ".wav")
        val wavFile = File(wavFileName)
        return wavFile.absolutePath
    }
}

