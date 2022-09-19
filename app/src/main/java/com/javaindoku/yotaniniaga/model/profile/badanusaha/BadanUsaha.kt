package com.javaindoku.yotaniniaga.model.profile.badanusaha


import com.google.gson.annotations.SerializedName

data class BadanUsaha(
    @SerializedName("data")
    var `data`: List<BadanUsahaData>? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)