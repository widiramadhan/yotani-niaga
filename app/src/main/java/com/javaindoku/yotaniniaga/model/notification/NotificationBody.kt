package com.javaindoku.yotaniniaga.model.notification

data class NotificationBody (
    var user_id: String = "",
    var type: String = "",
    var search: String= "",
    var page: String= "",
    var limit: String= ""
)