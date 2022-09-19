package com.javaindoku.yotaniniaga.model.gardenPetani

import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

class LandStatus(
    @SerializedName("status_lahan_id")
    var id: String? = "",
    @SerializedName("status_lahan_name")
    var name: String? = "",
) : AdapterDisplayText {
    override fun displayName(): String {
        return name.toString()
    }

    override fun selectId(): Any {
        return id.toString()
    }
}
