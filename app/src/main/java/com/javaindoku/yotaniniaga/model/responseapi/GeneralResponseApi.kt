package com.javaindoku.yotaniniaga.model.responseapi

import com.google.gson.annotations.SerializedName

data class GeneralResponseApi <T>(
    @SerializedName("data")
    var data: T? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean,
    @SerializedName("message")
    var message: String? = null
)