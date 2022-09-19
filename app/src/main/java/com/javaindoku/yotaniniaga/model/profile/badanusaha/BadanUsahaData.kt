package com.javaindoku.yotaniniaga.model.profile.badanusaha


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class BadanUsahaData(
    @SerializedName("badan usaha")
    var badanUsaha: String? = null,
    @SerializedName("desc_badan_usaha")
    var descBadanUsaha: String? = null,
    @SerializedName("parent_id")
    var parentId: Any? = null,
    @SerializedName("row_status")
    var rowStatus: Int? = null,
    @SerializedName("type_badan_usaha")
    var typeBadanUsaha: String? = null,
    @SerializedName("type_badan_usaha_id")
    var typeBadanUsahaId: Int? = null
) : AdapterDisplayText {
    override fun displayName(): String {
        return "$typeBadanUsaha"
    }

    override fun selectId(): Any {
        return typeBadanUsahaId.toString()
    }
}
