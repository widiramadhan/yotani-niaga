package com.javaindoku.yotaniniaga.model.otp


import com.google.gson.annotations.SerializedName

data class SendOtpData(
    @SerializedName("request_id")
    var request_id: String? = null
)