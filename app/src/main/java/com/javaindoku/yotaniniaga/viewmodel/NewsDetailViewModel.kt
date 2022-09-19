package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.news.News
import com.javaindoku.yotaniniaga.repository.newsdetail.NewsDetailRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import javax.inject.Inject

class NewsDetailViewModel @Inject constructor(private val repository: NewsDetailRepository) : BaseViewModel() {
    val newsResult = MutableLiveData<ApiResult<News>>()

    fun getNewsDetail(id: String) {
        repository.getNewsDetail(id).observeForever {
            newsResult.postValue(it)
        }
    }
}