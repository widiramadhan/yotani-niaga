package com.javaindoku.yotaniniaga.model.profile.provinsi


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class Data(
    @SerializedName("provinsi_id")
    var provinsiId: Int? = null,
    @SerializedName("provinsi_name")
    var provinsiName: String? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return provinsiName.toString()
    }

    override fun selectId(): Any {
        return provinsiId.toString()
    }
}