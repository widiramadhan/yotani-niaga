package com.javaindoku.yotaniniaga.model.invoice

import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.model.transactionfarmer.TransactionFarmer

/*
*
*
*@Author
*@Version
*/
data class InvoiceWrapper <T>(
    @SerializedName("data")
    val `data`: T,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String
)