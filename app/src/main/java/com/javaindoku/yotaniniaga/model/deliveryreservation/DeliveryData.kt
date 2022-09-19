package com.javaindoku.yotaniniaga.model.deliveryreservation


import com.google.gson.annotations.SerializedName

data class DeliveryData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("koperasi_id")
    val koperasiId: Any,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("nama_koperasi")
    val namaKoperasi: Any,
    @SerializedName("nama_pabrik")
    val namaPabrik: String,
    @SerializedName("no_reservasi")
    val noReservasi: String,
    @SerializedName("pabrik_id")
    val pabrikId: Int,
    @SerializedName("petani_id")
    val petaniId: Int,
    @SerializedName("row_status")
    val rowStatus: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("tanggal_pengiriman")
    val tanggalPengiriman: String,
    @SerializedName("tonasi")
    val tonasi: String,
    @SerializedName("foto")
    val foto: String,
    @SerializedName("status_name")
    val statusName: String,
    @SerializedName("no_invoice")
    val noInvoice: String,
)