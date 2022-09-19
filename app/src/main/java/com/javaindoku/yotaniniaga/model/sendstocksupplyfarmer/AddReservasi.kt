package com.javaindoku.yotaniniaga.model.sendstocksupplyfarmer


import com.google.gson.annotations.SerializedName

data class AddReservasi(
    @SerializedName("data")
    var `data`: List<DataConfirmReservasi>,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)