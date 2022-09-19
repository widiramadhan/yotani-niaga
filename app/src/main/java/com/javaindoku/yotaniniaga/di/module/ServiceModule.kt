package com.javaindoku.yotaniniaga.di.module

import com.javaindoku.yotaniniaga.service.firebase.MyFirebaseMessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

/*
*
*
*@Author
*@Version
*/
@Module
abstract class ServiceModule {
    @ContributesAndroidInjector
    abstract fun contributeFirebaseService() : MyFirebaseMessagingService
}