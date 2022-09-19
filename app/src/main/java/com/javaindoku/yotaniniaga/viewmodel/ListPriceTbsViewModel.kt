package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.repository.listpricetbs.PriceRepository
import javax.inject.Inject

class ListPriceTbsViewModel @Inject constructor(private val repository: PriceRepository) : BaseViewModel() {
    val pageInit = MutableLiveData<Int>()

    private val repoResult = Transformations.map(pageInit) {
        repository.getPrices()
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