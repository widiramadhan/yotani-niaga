package com.javaindoku.yotaniniaga.model.deliveryreservation


import com.google.gson.annotations.SerializedName

data class DeliveryReservationEditBody(
    @SerializedName("no_reservasi")
    val noReservasi: String,
    @SerializedName("petani_id")
    val petaniId: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("tanggal_pengiriman")
    val tanggalPengiriman: String,
    @SerializedName("type_user")
    val typeUser: String,
    @SerializedName("user_id")
    val userId: String
)