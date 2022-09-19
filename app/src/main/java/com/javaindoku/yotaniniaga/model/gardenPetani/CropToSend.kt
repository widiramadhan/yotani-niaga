package com.javaindoku.yotaniniaga.model.gardenPetani

import com.javaindoku.yotaniniaga.model.profile.kelurahan.Kelurahan
import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

/*
*
*
*@Author
*@Version
*/
data class CropToSend (
    var id: String = "",
    var jenisBibit: String = "",
    var luas: String = "",
    var alamat: String = "",
    var kabupaten: String = "",
    var kelurahan: String = "",
    var kecamatan: String = "",
    var petaniId: String = "",
    var petaniName: String = "",
    var tonasi: Int = 0
) : AdapterDisplayText {
    override fun displayName(): String {
        return "$jenisBibit $kelurahan"
    }

    override fun selectId(): Any {
        return id
    }
}