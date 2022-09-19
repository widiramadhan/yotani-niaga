package com.javaindoku.yotaniniaga.model.profile.provinsi


import com.google.gson.annotations.SerializedName

data class Provinsi(
    @SerializedName("data")
    var `data`: List<Data>? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)