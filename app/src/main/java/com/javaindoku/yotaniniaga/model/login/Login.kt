package com.javaindoku.yotaniniaga.model.login


import com.google.gson.annotations.SerializedName

data class Login(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String
)