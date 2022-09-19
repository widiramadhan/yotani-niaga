package com.javaindoku.yotaniniaga.model.profile.profesi


import com.google.gson.annotations.SerializedName

data class Profesi(
    @SerializedName("data")
    var `data`: List<DataProfesi>? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)