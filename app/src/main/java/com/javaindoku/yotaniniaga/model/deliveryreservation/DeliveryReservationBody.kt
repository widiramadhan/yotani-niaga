package com.javaindoku.yotaniniaga.model.deliveryreservation


import com.google.gson.annotations.SerializedName

data class DeliveryReservationBody(
    @SerializedName("page")
    var page: Long = 0,
    @SerializedName("sort")
    var sort: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("type_user")
    var typeUser: String = "",
    @SerializedName("user_id")
    var userId: String = ""
)