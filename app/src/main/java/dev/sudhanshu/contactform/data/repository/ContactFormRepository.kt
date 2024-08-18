package dev.sudhanshu.contactform.data.repository


import android.os.Environment
import com.google.gson.Gson
import dev.sudhanshu.contactform.data.model.FormData
import dev.sudhanshu.contactform.util.ActivityContextHolder
import dev.sudhanshu.contactform.util.FileUtils

class ContactFormRepository() {


    fun getAllJsonFileNames(): List<String> {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val jsonFiles = downloadDir.listFiles { _, name ->
           name.startsWith("contactForm_") && name.endsWith(".json")
        }

        return jsonFiles?.map { it.name } ?: emptyList()
    }

    fun saveFormData(formData: FormData){
        ActivityContextHolder.getActivityContext()?.let { FileUtils.saveDataToFile(it, formData) }
    }
}
