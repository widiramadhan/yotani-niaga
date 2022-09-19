package com.javaindoku.yotaniniaga.repository.profilePetani

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.service.network.Api

class ProfilePetaniDataSourceFactory(private val api: Api, private val gardenBody: GardenBody, private val token: String) : DataSource.Factory<Int, GardenDataSchema>() {
    val sourceLiveData = MutableLiveData<ProfilePetaniDataSource>()
    override fun create(): DataSource<Int, GardenDataSchema> {
        val itemDataSource = ProfilePetaniDataSource(api, gardenBody, token)
        sourceLiveData.postValue(itemDataSource)
        return itemDataSource
    }
}