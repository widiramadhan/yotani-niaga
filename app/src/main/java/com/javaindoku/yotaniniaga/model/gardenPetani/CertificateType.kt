package com.javaindoku.yotaniniaga.model.gardenPetani

import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

class CertificateType (
    @SerializedName("sertifikasi_id")
    var id: String? = "",
    @SerializedName("sertifikasi_name")
    var name: String? = ""
) : AdapterDisplayText {
    override fun displayName(): String {
        return "$name"
    }

    override fun selectId(): Any {
        return id.toString()
    }
}
