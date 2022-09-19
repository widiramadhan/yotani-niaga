package com.javaindoku.yotaniniaga.repository.transactionkoperasi

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.javaindoku.yotaniniaga.model.profile.Farmer
import com.javaindoku.yotaniniaga.model.transaction.TransactionKoperasi
import com.javaindoku.yotaniniaga.repository.profilekoperasi.ProfileKoperasiDataSource

class TransactionKoperasiDataSourceFactory () : DataSource.Factory<Int, TransactionKoperasi>() {
    val sourceLiveData = MutableLiveData<TransactionKoperasiDataSource>()
    override fun create(): DataSource<Int, TransactionKoperasi> {
        val itemDataSource =
            TransactionKoperasiDataSource()
        sourceLiveData.postValue(itemDataSource)
        return itemDataSource
    }
}