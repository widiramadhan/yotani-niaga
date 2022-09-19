package com.javaindoku.yotaniniaga.model.deliveryreservation


import com.google.gson.annotations.SerializedName

data class DeliveryReservation(
    @SerializedName("data")
    val `data`: List<DeliveryData>,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("totalData")
    val totalData: Int,
    @SerializedName("totalPage")
    val totalPage: Int
)