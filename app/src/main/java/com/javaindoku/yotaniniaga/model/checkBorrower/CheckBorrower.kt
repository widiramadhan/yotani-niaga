package com.javaindoku.yotaniniaga.model.checkBorrower


import com.google.gson.annotations.SerializedName

data class CheckBorrower(
    @SerializedName("data")
    var `data`: CheckBorrowerData? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)