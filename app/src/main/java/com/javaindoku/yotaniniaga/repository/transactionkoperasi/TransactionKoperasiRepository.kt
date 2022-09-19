package com.javaindoku.yotaniniaga.repository.transactionkoperasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.javaindoku.yotaniniaga.model.profile.Farmer
import com.javaindoku.yotaniniaga.model.transaction.TransactionKoperasi
import com.javaindoku.yotaniniaga.repository.profilekoperasi.ProfileKoperasiDataSourceFactory
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.NetworkState
import javax.inject.Inject

class TransactionKoperasiRepository @Inject constructor(private val api: Api) {
    fun getTransactionKoperasi(): Listing<TransactionKoperasi> {
        val sourceFactory = TransactionKoperasiDataSourceFactory()
        val config: PagedList.Config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(30).build()
        val liveData: LiveData<PagedList<TransactionKoperasi>> = LivePagedListBuilder(sourceFactory, config).build()
        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
            it.networkState
        }

        return Listing(
            pagedList = liveData,
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.networkState
            },
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFiled()
            },
            refreshState = refreshState
        )
    }

    data class Listing<T>(
        val pagedList: LiveData<PagedList<T>>,
        val networkState: LiveData<NetworkState>,
        val refreshState: LiveData<NetworkState>,
        val refresh: () -> Unit,
        val retry: () -> Unit
    )
}