package com.javaindoku.yotaniniaga.model.profile.pendidikan


import com.google.gson.annotations.SerializedName

data class Pendidikan(
    @SerializedName("data")
    var `data`: List<DataPendidikan>? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)