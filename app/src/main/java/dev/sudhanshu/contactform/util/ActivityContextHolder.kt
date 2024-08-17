package dev.sudhanshu.contactform.util

import android.app.Activity
import java.lang.ref.WeakReference

object ActivityContextHolder {

    private var activityContext: WeakReference<Activity>? = null

    fun setActivityContext(activity: Activity) {
        activityContext = WeakReference(activity)
    }

    fun getActivityContext(): Activity? {
        return activityContext?.get()
    }

    fun clearActivityContext() {
        activityContext?.clear()
        activityContext = null
    }
}
