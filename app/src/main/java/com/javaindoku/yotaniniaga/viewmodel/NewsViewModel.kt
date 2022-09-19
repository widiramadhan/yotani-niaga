package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.repository.news.NewsRepository
import javax.inject.Inject

class NewsViewModel @Inject constructor(private val repository: NewsRepository) : BaseViewModel() {
    var totalScrollYRecyclerView = 0

    val pageInit = MutableLiveData<Int>()

    private val repoResult = Transformations.map(pageInit) {
        repository.getNews()
    }

    val pos = Transformations.switchMap(repoResult) {
        it.pagedList
    }

    val networkState = Transformations.switchMap(repoResult) {
        it.networkState
    }

    fun retry() {
        val listing = repoResult.value
        listing?.retry?.invoke()
    }

}