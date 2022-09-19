package com.javaindoku.yotaniniaga.model.profilePetaniDetail


import com.google.gson.annotations.SerializedName

data class ProfilePetaniDetail(
    @SerializedName("data")
    var `data`: Data? = null,
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)