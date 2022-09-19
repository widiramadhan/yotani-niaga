package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.repository.profilekoperasi.ProfileKoperasiRepository
import com.javaindoku.yotaniniaga.repository.transactionkoperasi.TransactionKoperasiRepository
import javax.inject.Inject

class TransactionKoperasiViewModel @Inject constructor(private val repository: TransactionKoperasiRepository) : BaseViewModel() {
    val pageInit = MutableLiveData<Int>()

    private val repoResult = Transformations.map(pageInit) {
        repository.getTransactionKoperasi()
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

    fun refresh(){
        repoResult.value?.refresh?.invoke()
    }

}