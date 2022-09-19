package com.javaindoku.yotaniniaga.repository.profilekoperasi

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.javaindoku.yotaniniaga.model.profile.Farmer
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.Data
import com.javaindoku.yotaniniaga.model.profilePetaniDetail.ProfilePetaniDetail
import com.javaindoku.yotaniniaga.service.network.Api
import org.json.JSONObject

class ProfileKoperasiDataSourceFactory(val api: Api, val jsonBody: JSONObject, val token: String) :
    DataSource.Factory<Int, Data>() {
    val sourceLiveData = MutableLiveData<ProfileKoperasiDataSource>()
    override fun create(): DataSource<Int, Data> {
        val itemDataSource =
            ProfileKoperasiDataSource(api, jsonBody, token)
        sourceLiveData.postValue(itemDataSource)
        return itemDataSource
    }
}