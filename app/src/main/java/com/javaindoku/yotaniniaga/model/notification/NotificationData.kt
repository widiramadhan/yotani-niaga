package com.javaindoku.yotaniniaga.model.notification


import com.google.gson.annotations.SerializedName

data class NotificationData(
    @SerializedName("created_by")
    var createdBy: String? = "",
    @SerializedName("created_date")
    var createdDate: String? = "",
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("read")
    var read: String? = "",
    @SerializedName("row_status")
    var rowStatus: String? = "",
    @SerializedName("title")
    var title: String? = ""
)