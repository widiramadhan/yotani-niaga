package com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer


import com.google.gson.annotations.SerializedName

data class DataConfirmReservasi(
    @SerializedName("nama_petani")
    var namaPetani: String? = null,
    @SerializedName("no_grn")
    var noGrn: String? = null,
    @SerializedName("tanggal_terima")
    var tanggalTerima: String? = null,
    @SerializedName("total_tonasi")
    var totalTonasi: String? = null
)