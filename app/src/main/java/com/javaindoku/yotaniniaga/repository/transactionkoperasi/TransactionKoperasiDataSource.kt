package com.javaindoku.yotaniniaga.repository.transactionkoperasi

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.javaindoku.yotaniniaga.model.profile.Farmer
import com.javaindoku.yotaniniaga.model.transaction.TransactionKoperasi
import com.javaindoku.yotaniniaga.service.network.NetworkState
import kotlinx.coroutines.*

class TransactionKoperasiDataSource (
) : PageKeyedDataSource<Int, TransactionKoperasi>() {


    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()
    var pageIndex = 1
    val totalPage = MutableLiveData<Int>()
    val lastNumber = MutableLiveData(0)

    private var retry: (() -> Any)? = null


    fun retryAllFiled() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            CoroutineScope(Dispatchers.IO).launch {
                it.invoke()
            }

        }

    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, TransactionKoperasi>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        GlobalScope.launch {
            delay(3000L)
            callback.onResult(generateListData(), null, 1)
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            pageIndex++
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, TransactionKoperasi>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        GlobalScope.launch {
            delay(3000L)
            callback.onResult(generateListData(), pageIndex)
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            pageIndex++
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, TransactionKoperasi>
    ) {

    }


    private fun generateListData(): List<TransactionKoperasi> {
        val tmp = mutableListOf<TransactionKoperasi>()
        for(i in 1..30) {
            tmp.add(TransactionKoperasi(
                id = "$i",
                companyName = "PT Indocyber Global $i",
                weight = "${i}000",
                unitWeight = "Ton",
                price = "${i}0000",
                distance = "0.${i}",
                image = "https://ssl.gstatic.com/ui/v1/icons/mail/rfr/logo_gmail_lockup_default_1x.png",
                unitDistance = "Km"
            ))
        }
        return tmp
    }

}