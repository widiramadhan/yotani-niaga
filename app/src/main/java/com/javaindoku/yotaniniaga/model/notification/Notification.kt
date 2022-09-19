package com.javaindoku.yotaniniaga.model.notification


import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("data")
    var `data`: List<NotificationData>,
    @SerializedName("isSuccess")
    var isSuccess: Boolean,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("totalData")
    var totalData: Int? = null,
    @SerializedName("totalPage")
    var totalPage: Int? = null,
    @SerializedName("totalRead")
    var totalRead: Int? = null,
    @SerializedName("totalUnread")
    var totalUnread: Int? = null
)