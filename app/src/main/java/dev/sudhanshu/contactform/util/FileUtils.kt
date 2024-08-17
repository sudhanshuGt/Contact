package dev.sudhanshu.contactform.util


import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import dev.sudhanshu.contactform.data.model.FormData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



object FileUtils {

    fun saveDataToFile(context: Context, formData: FormData) {
        val dateFormat = SimpleDateFormat("dd_MM_yyyy_hh_mm_a", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadDir, "contactForm_$currentDate.json")

        try {
            // Writing JSON data to the file
            val json = Gson().toJson(formData)
            val fos = FileOutputStream(file, true)
            fos.write((json + "\n").toByteArray())
            fos.close()

            Log.d("FileUtils", "File saved to ${file.absolutePath}")

        } catch (e: IOException) {
            Log.e("FileUtils", "Failed to save file: ${e.message}", e)
        }
    }
}

