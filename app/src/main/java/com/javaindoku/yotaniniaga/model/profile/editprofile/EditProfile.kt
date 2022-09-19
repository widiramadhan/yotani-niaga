package com.javaindoku.yotaniniaga.model.profile.editprofile


import com.google.gson.annotations.SerializedName

data class EditProfile(
    /*@SerializedName("data")
    var `data`: List<Any>? = null,*/
    @SerializedName("isSuccess")
    var isSuccess: Boolean? = null,
    @SerializedName("message")
    var message: String? = null
)