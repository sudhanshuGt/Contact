package dev.sudhanshu.contactform.data.repository


import android.os.Environment
import com.google.gson.Gson
import dev.sudhanshu.contactform.data.model.FormData
import dev.sudhanshu.contactform.util.ActivityContextHolder
import dev.sudhanshu.contactform.util.FileUtils

class ContactFormRepository() {


    private val gson = Gson()


    fun getAllForms(): List<FormData> {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val files = downloadDir.listFiles { file ->
            file.name.matches(Regex("contactForm_\\d{2}_\\d{2}_\\d{4}_\\d{2}_\\d{2}_[AP]M\\.json"))
        }?.sortedByDescending { it.lastModified() } ?: return emptyList()

        val formList = mutableListOf<FormData>()
        files.forEach { file ->
            val json = file.readText()
            val formData = gson.fromJson(json, FormData::class.java)
            formList.add(formData)
        }
        return formList
    }

    fun saveFormData(formData: FormData) {
        ActivityContextHolder.getActivityContext()?.let { FileUtils.saveDataToFile(it, formData) }
    }
}
