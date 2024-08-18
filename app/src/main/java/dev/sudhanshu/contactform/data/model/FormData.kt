package dev.sudhanshu.contactform.data.model

import com.google.gson.annotations.SerializedName


data class FormData(
    @SerializedName("Q1") val age: Int,
    @SerializedName("Q2") val selfiePath: String,
    @SerializedName("recording") val recordingPath: String,
    @SerializedName("submit_time") val submitTime: String
)