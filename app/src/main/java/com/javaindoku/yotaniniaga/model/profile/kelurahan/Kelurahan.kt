package com.javaindoku.yotaniniaga.model.profile.kelurahan


import com.google.gson.annotations.SerializedName

data class Kelurahan(
    @SerializedName("data")
    var `data`: List<Data>? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)