package com.javaindoku.yotaniniaga.model.profile.kabupaten


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class Data(
    @SerializedName("kabupaten_kota_id")
    var kabupatenKotaId: Int? = null,
    @SerializedName("kabupaten_kota_name")
    var kabupatenKotaName: String? = null,
    @SerializedName("provinsi_id")
    var provinsiId: Int? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return kabupatenKotaName.toString()
    }

    override fun selectId(): Any {
        return kabupatenKotaId.toString()
    }
}