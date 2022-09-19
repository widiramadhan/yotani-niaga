package com.javaindoku.yotaniniaga.model.news


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("author")
    val author: String,
    @SerializedName("language_id")
    val languageId: String,
    @SerializedName("news_date")
    val newsDate: String,
    @SerializedName("news_id")
    val newsId: String,
    @SerializedName("news_image")
    val newsImage: String,
    @SerializedName("news_title")
    val newsTitle: String,
    @SerializedName("row_status")
    val rowStatus: String
)