package com.javaindoku.yotaniniaga.repository.news

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.model.news.NewsData
import com.javaindoku.yotaniniaga.service.network.Api
import com.javaindoku.yotaniniaga.service.network.NetworkState
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.net.UnknownHostException

class NewsDataSource (private val api: Api) : PageKeyedDataSource<Int, NewsData>() {


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
        callback: LoadInitialCallback<Int, NewsData>
    ) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)
        GlobalScope.launch {
            try {
                val response = api.getNews(pageIndex.toString())
                if(response.isSuccess)
                {
                    callback.onResult(response.data, null, 2)
                    initialLoad.postValue(NetworkState.LOADED)
                }
                else
                {
                    networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    initialLoad.postValue(NetworkState.LOADED)
                }
            }
            catch (e: Exception)
            {
                when(e) {
                    is UnknownHostException -> {
                        networkState.postValue(NetworkState.errorInt(R.string.srSomethingWentWrongPleaseTryAgainLater))
                    }
                    is HttpException -> {
                        networkState.postValue(NetworkState.errorInt(R.string.srServerError))
                    }
                    else -> {
                        networkState.postValue(NetworkState.errorInt(R.string.srServerError))
                    }
                }
                initialLoad.postValue(NetworkState.LOADED)
            }
            pageIndex++

//            delay(3000L)
//            callback.onResult(generateListData(), null, 1)
//            networkState.postValue(NetworkState.LOADED)
//            initialLoad.postValue(NetworkState.LOADED)
//            pageIndex++
        }
    }

    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, NewsData>
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
        callback: LoadCallback<Int, NewsData>
    ) {

    }


    private var c = 0
    private fun generateListData(): List<NewsData> {
        val tmp = mutableListOf<NewsData>()
//        for(i in 0..30) {
//            if(i%2==0)
//            {
//                tmp.add(NewsData(1, "Indonesia adalah negara dengan berbagai macam kearifan lokal", "2020-08-21 15:48:33 GMT+07:00", "https://ci5.googleusercontent.com/proxy/mJfD7nMlcO_OaeYD6lWd1Fm8movsyqrQeb7bU9TIneK1Rx8tTS8L0qA_qolvMvelk3hNO_fBsJ5TL-65nKKlx7ca94bKIplYclncFF7cQEwdSbhuqm1o9iiA=s0-d-e1-ft#https://www.bca.co.id/~/media/Images/e-info2020/20200812-elc-slice1.jpg"))
//            }
//            else
//            {
//                tmp.add(NewsData(2, "Indonesia adalah berdaulat adil dan makmur", "2020-08-21 15:48:33 GMT+07:00", "https://ssl.gstatic.com/ui/v1/icons/mail/rfr/logo_gmail_lockup_default_1x.png"))
//            }
//        }
        return tmp
    }

}