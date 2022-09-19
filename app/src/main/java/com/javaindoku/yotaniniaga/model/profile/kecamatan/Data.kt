package com.javaindoku.yotaniniaga.model.profile.kecamatan


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class Data(
    @SerializedName("kabupaten_kota_id")
    var kabupatenKotaId: Int? = null,
    @SerializedName("kecamatan_id")
    var kecamatanId: Int? = null,
    @SerializedName("kecamatan_name")
    var kecamatanName: String? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return kecamatanName.toString()
    }

    override fun selectId(): Any {
        return kecamatanId.toString()
    }
}

