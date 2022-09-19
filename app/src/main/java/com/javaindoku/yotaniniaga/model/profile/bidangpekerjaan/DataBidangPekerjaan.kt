package com.javaindoku.yotaniniaga.model.profile.bidangpekerjaan


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class DataBidangPekerjaan(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("nama")
    var nama: String? = null,
    @SerializedName("deskripsi")
    var deskripsi: String? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return nama.toString()
    }

    override fun selectId(): Any {
        return id.toString()
    }
}