package com.javaindoku.yotaniniaga.model.checkGarden


import com.google.gson.annotations.SerializedName

data class CheckGarden(
    @SerializedName("data")
    var `data`: CheckGardenData? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)