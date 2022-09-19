package com.javaindoku.yotaniniaga.model.profile.agama


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class DataAgama(
    @SerializedName("agama_id")
    var agama_id: String? = null,
    @SerializedName("agama_deskripsi")
    var agama_deskripsi: String? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return agama_deskripsi.toString()
    }

    override fun selectId(): Any {
        return agama_id.toString()
    }
}