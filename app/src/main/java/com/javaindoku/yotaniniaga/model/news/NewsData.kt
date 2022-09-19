package com.javaindoku.yotaniniaga.model.news

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NewsData (
//    @SerializedName("news_id")
//    var newsId : Int = 0,
//    @SerializedName("news_title")
//    var newsTitle : String = "",
//    @SerializedName("news_date")
//    var newsDate : String = "",
//    @SerializedName("news_image")
//    var newsImage : String = ""

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
    val rowStatus: String,
    @SerializedName("news_description")
    val newsDescription: String

) : Serializable