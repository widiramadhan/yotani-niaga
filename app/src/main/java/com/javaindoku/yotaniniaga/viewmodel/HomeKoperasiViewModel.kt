package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenKoperasiBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenPetani
import com.javaindoku.yotaniniaga.model.news.News
import com.javaindoku.yotaniniaga.repository.homekoperasi.HomeKoperasiRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import javax.inject.Inject

class HomeKoperasiViewModel @Inject constructor(private val repository: HomeKoperasiRepository) : BaseViewModel() {
    var gardenKoperasiBody = GardenKoperasiBody()
    val newsResult = MutableLiveData<ApiResult<News>>()
    val gardenResult = MutableLiveData<ApiResult<GardenPetani>>()
    var gardenBody = GardenBody()

    fun getNews() {
        repository.getNews().observeForever {
            newsResult.postValue(it)
        }
    }

    fun getGardenPetani(token: String) {
        repository.getGarden(gardenBody, token).observeForever {
            gardenResult.postValue(it)
        }
    }

    fun getGardenKoperasi(token: String) {
        repository.getGardenKoperasi(gardenKoperasiBody, token).observeForever {
            gardenResult.postValue(it)
        }
    }
}