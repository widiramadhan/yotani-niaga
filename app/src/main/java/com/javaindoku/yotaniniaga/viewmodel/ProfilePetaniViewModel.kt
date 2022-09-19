package com.javaindoku.yotaniniaga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.javaindoku.yotaniniaga.base.BaseViewModel
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenBody
import com.javaindoku.yotaniniaga.model.responseapi.ResponseApi
import com.javaindoku.yotaniniaga.repository.profilePetani.ProfilePetaniRepository
import com.javaindoku.yotaniniaga.service.network.ApiResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import javax.inject.Inject

class ProfilePetaniViewModel @Inject constructor(private val repository: ProfilePetaniRepository) : BaseViewModel() {
    val pageInit = MutableLiveData<Int>()
    var actionResult = MutableLiveData<ApiResult<ResponseApi>>()
    var gardenBody = GardenBody()
    var token: String = ""
    var totalScrollYRecyclerView = 0

    private val repoResult = Transformations.map(pageInit) {
        repository.getGardenPetani(gardenBody, token)
    }

    fun deleteGardenById(token: String, userId: String, kebunId: String) {
        val jsonBody = JSONObject()
        jsonBody.put("user_id", userId)
        jsonBody.put("kebun_id", kebunId)
        val body = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        repository.deleteGarden(token, body).observeForever {
            actionResult.postValue(it)
        }
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