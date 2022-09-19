package com.javaindoku.yotaniniaga.model.responseapi

import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.model.login.Data

data class ResponseApi (
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String
)