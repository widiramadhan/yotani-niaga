package com.javaindoku.yotaniniaga.repository.transactionfarmer

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.javaindoku.yotaniniaga.model.factory.FactoryBody
import com.javaindoku.yotaniniaga.model.transactionfarmer.TransactionFarmer
import com.javaindoku.yotaniniaga.service.network.Api

class TransactionFarmerDataSourceFactory(private val api: Api, private val factoryBody: FactoryBody, private val token: String) : DataSource.Factory<Int, TransactionFarmer>() {
    val sourceLiveData = MutableLiveData<TransactionFarmerDataSource>()
    override fun create(): DataSource<Int, TransactionFarmer> {
        val itemDataSource =
            TransactionFarmerDataSource(api, factoryBody, token)
        sourceLiveData.postValue(itemDataSource)
        return itemDataSource
    }
}