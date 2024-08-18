package dev.sudhanshu.contactform.util

import android.app.Activity
import java.lang.ref.WeakReference

object FileHolder {

    private var fileName: WeakReference<String>? = null

    fun setFileName(fileName: String) {
        this.fileName = WeakReference(fileName)
    }

    fun getFileName(): String? {
        return fileName?.get()
    }

    fun clearFile() {
        fileName?.clear()
    }
}
