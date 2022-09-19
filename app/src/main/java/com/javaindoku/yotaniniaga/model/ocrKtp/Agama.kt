package com.javaindoku.yotaniniaga.model.ocrKtp


import com.google.gson.annotations.SerializedName

data class Agama(
    @SerializedName("confidence")
    var confidence: Int? = null,
    @SerializedName("order")
    var order: Int? = null,
    @SerializedName("value")
    var value: String? = null
)