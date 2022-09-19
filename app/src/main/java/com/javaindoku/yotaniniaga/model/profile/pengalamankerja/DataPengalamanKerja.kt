package com.javaindoku.yotaniniaga.model.profile.pengalamankerja


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class DataPengalamanKerja(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("deskripsi")
    var deskripsi: String? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return deskripsi.toString()
    }

    override fun selectId(): Any {
        return id.toString()
    }
}