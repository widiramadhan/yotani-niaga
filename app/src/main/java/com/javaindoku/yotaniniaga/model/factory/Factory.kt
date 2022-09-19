package com.javaindoku.yotaniniaga.model.factory


import com.google.gson.annotations.SerializedName
import com.javaindoku.yotaniniaga.model.transactionfarmer.TransactionFarmer

data class Factory(
    @SerializedName("data")
    val `data`: List<TransactionFarmer>,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String
)