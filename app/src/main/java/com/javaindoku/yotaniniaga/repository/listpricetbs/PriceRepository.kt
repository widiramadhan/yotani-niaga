package com.javaindoku.yotaniniaga.repository.listpricetbs

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.view.pricetbs.PriceTbs
import javax.inject.Inject

class PriceRepository @Inject constructor(private val api: Api) {
    fun getPrices(): Listing<PriceTbs> {
        val sourceFactory = PriceDataSourceFactory()
        val config: PagedList.Config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(30).build()
        val liveData: LiveData<PagedList<PriceTbs>> = LivePagedListBuilder(sourceFactory, config).build()
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