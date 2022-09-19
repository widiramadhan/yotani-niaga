package com.javaindoku.yotaniniaga.repository.profilePetani

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.google.gson.Gson
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenBody
import com.javaindoku.yotaniniaga.model.gardenPetani.GardenDataSchema
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.NetworkState
import com.javaindoku.yotaniniaga.utilities.authorization
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

class ProfilePetaniDataSource(
    private val api: Api,
    private val gardenBody: GardenBody,
    private val token: String
) : PageKeyedDataSource<Int, GardenDataSchema>() {

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
        callback: LoadInitialCallback<Int, GardenDataSchema>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        GlobalScope.launch {
            /*delay(3000L)
            callback.onResult(generateListData(), null, 1)
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            pageIndex++*/

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val body = Gson().toJson(gardenBody).toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    val response = api.getGardenPetani(body, token.authorization())
                    if (response.isSuccess) {
                        callback.onResult(response.data, null, null)
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        if (response.message.equals("Password don`t match", true)) {
                        } else if (response.message.equals(
                                "User not found or Invalid Email",
                                true
                            )
                        ) {
                        } else {
                        }
                    }
                } catch (e: Exception) {
                    when (e) {
                        is UnknownHostException -> {
                            networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                        }
                        is HttpException -> {
                            networkState.postValue(NetworkState.errorInt(R.string.srServerError))
                        }
                        is ConnectException -> {
                            networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                        }
                        else -> {
                            networkState.postValue(NetworkState.errorInt(R.string.srServerError))
                        }
                    }
                }
            }
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, GardenDataSchema>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        GlobalScope.launch {
            /*delay(3000L)
            callback.onResult(generateListData(), pageIndex)
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            pageIndex++*/
        }
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, GardenDataSchema>
    ) {
    }

    private fun generateListData(): List<GardenDataSchema> {
        val tmp = mutableListOf<GardenDataSchema>()
        /*for(i in 0..30) {
            tmp.add(Farmer("$i", "Luas Kebun $i hektar", "Potensi Produksi $i Kg / Bulan 100 pohon Tahun 200$i kondisi lahan Gambut"))
        }*/
        return tmp
    }

}