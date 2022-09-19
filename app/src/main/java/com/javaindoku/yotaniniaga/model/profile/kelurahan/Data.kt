package com.javaindoku.yotaniniaga.model.profile.kelurahan


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class Data(
    @SerializedName("kecamatan_id")
    var kecamatanId: Int? = null,
    @SerializedName("kelurahan_desa_id")
    var kelurahanDesaId: Long? = null,
    @SerializedName("kelurahan_desa_name")
    var kelurahanDesaName: String? = null,
    @SerializedName("kode_pos")
    var kodePos: Int? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return kelurahanDesaName.toString()
    }

    override fun selectId(): Any {
        return kelurahanDesaId.toString()
    }
}