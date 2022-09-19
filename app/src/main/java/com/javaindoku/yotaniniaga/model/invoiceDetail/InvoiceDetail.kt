package com.javaindoku.yotaniniaga.model.invoiceDetail


import com.google.gson.annotations.SerializedName

data class InvoiceDetail(
    @SerializedName("data")
    var `data`: InvoiceDetailData? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)