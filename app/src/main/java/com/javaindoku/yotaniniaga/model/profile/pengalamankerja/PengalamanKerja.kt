package com.javaindoku.yotaniniaga.model.profile.pengalamankerja


import com.google.gson.annotations.SerializedName

data class PengalamanKerja(
    @SerializedName("data")
    var `data`: List<DataPengalamanKerja>? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)