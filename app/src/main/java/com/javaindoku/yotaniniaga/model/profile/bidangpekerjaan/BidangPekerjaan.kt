package com.javaindoku.yotaniniaga.model.profile.bidangpekerjaan


import com.google.gson.annotations.SerializedName

data class BidangPekerjaan(
    @SerializedName("data")
    var `data`: List<DataBidangPekerjaan>? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)