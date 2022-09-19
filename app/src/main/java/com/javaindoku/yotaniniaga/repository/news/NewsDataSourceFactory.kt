package com.javaindoku.yotaniniaga.repository.news

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.javaindoku.yotaniniaga.model.news.NewsData
import com.javaindoku.yotaniniaga.service.network.Api

class NewsDataSourceFactory(private val api: Api) : DataSource.Factory<Int, NewsData>() {
    val sourceLiveData = MutableLiveData<NewsDataSource>()
    override fun create(): DataSource<Int, NewsData> {
        val itemDataSource =
            NewsDataSource(api)
        sourceLiveData.postValue(itemDataSource)
        return itemDataSource
    }
}