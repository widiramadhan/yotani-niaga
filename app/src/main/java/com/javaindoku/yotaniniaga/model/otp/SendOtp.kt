package com.javaindoku.yotaniniaga.model.otp


import com.google.gson.annotations.SerializedName

data class SendOtp(
    @SerializedName("data")
    var `data`: SendOtpData? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)