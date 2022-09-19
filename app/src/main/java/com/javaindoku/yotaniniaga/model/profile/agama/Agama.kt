package com.javaindoku.yotaniniaga.model.profile.agama


import com.google.gson.annotations.SerializedName

data class Agama(
    @SerializedName("data")
    var `data`: List<DataAgama>? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)