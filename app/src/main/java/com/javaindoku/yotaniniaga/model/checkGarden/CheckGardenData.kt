package com.javaindoku.yotaniniaga.model.checkGarden


import com.google.gson.annotations.SerializedName

data class CheckGardenData(
    @SerializedName("check_kebun_koperasi")
    var checkKebunKoperasi: Boolean? = null,
    @SerializedName("jumlah_kebun")
    var jumlahKebun: Int? = null
)