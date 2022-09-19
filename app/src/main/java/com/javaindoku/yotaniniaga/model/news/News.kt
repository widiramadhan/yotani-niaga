package com.javaindoku.yotaniniaga.model.news


import com.google.gson.annotations.SerializedName

data class News(
    @SerializedName("data")
    val `data`: List<NewsData>,
    @SerializedName("isSuccess")
    val isSuccess: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("totalData")
    val totalData: Int,
    @SerializedName("totalPage")
    val totalPage: Int
)