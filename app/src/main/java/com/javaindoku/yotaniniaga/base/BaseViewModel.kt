package com.javaindoku.yotaniniaga.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.javaindoku.yotaniniaga.service.network.Api
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

open class BaseViewModel : ViewModel(), HasAndroidInjector {
    @Inject
    lateinit var api: Api

    var isLoading = MutableLiveData<Boolean?>()

    @Inject
    lateinit var dispactchingActivityInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispactchingActivityInjector

}