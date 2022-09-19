package com.javaindoku.yotaniniaga.model.login


import com.google.gson.annotations.SerializedName

data class DetailLogin(
    @SerializedName("latitude")
    var latitude: String? = "",
    @SerializedName("longitude")
    var longitude: String? = "",
    @SerializedName("petani_id")
    var petaniId: String? = "",
    @SerializedName("koperasi_id")
    var koperasiId: String? = ""
)