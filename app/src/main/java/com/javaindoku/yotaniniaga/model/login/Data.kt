package com.javaindoku.yotaniniaga.model.login


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("login_type")
    var loginType: String? = "",
    @SerializedName("type_user")
    var typeUser: String? = "",
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("username")
    var username: String? = "",
    @SerializedName("image_profile")
    var imageProfile: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("token")
    val token: String,
    @SerializedName("detail")
    val detailLogin: DetailLogin
)