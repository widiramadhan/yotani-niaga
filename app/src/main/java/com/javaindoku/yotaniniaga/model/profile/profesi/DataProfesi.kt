package com.javaindoku.yotaniniaga.model.profile.profesi


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class DataProfesi(
    @SerializedName("pekerjaan_id")
    var pekerjaan_id: String? = null,
    @SerializedName("pekerjaan_deskripsi")
    var pekerjaan_deskripsi: String? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return pekerjaan_deskripsi.toString()
    }

    override fun selectId(): Any {
        return pekerjaan_id.toString()
    }
}