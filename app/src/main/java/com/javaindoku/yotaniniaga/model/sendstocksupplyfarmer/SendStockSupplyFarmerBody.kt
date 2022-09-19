package com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer


import com.google.gson.annotations.SerializedName

data class SendStockSupplyFarmerBody(
    @SerializedName("pabrik_id")
    val pabrikId: String,
    @SerializedName("petani_id")
    val petaniId: String,
    @SerializedName("koperasi_id")
    val koperasiId: String,
    @SerializedName("tanggal_pengiriman")
    val tanggalPengiriman: String,
    @SerializedName("tonasi")
    val tonasi: String,
    @SerializedName("user_id")
    val userId: String
)