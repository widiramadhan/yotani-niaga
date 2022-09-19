package com.javaindoku.yotaniniaga.model.gardenPetani

import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

class DocumentType (
    @SerializedName("dokumen_id")
    var dokumen_id: String? = "",
    @SerializedName("dokumen_name")
    var dokumen_name: String? = "",
    @SerializedName("dokumen_jenis")
    var dokumen_jenis: String? = "",
    @SerializedName("dokumen_no_blanko")
    var dokumen_no_blanko: String? = "",
    @SerializedName("dokumen_berlaku_dari")
    var dokumen_berlaku_dari: String? = "",
    @SerializedName("dokumen_berlaku_sampai")
    var dokumen_berlaku_sampai: String? = "",
) : AdapterDisplayText {
    override fun displayName(): String {
        return "$dokumen_name"
    }

    override fun selectId(): Any {
        return dokumen_id.toString()
    }
}

