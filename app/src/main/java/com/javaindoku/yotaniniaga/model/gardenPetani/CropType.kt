package com.javaindoku.yotaniniaga.model.gardenPetani

import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

class CropType (
    @SerializedName("jenis_bibit_id")
    var id: String? = "",
    @SerializedName("jenis_bibit")
    var type: String? = "",
    @SerializedName("jenis_bibit_name")
    var name: String? = "",
    @SerializedName("keterangan_bibit")
    var description: String? = "",
) : AdapterDisplayText {
    override fun displayName(): String {
        return "$name"
    }

    override fun selectId(): Any {
        return id.toString()
    }
}
