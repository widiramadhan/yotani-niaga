package com.javaindoku.yotaniniaga.di.module

import android.app.Application
import android.content.Context
import com.javaindoku.yotaniniaga.MainApplication
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.PrefHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {
@Singleton
@Provides
fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    fun provideMainApplication(application: Application): MainApplication = application as MainApplication

    @Provides
    fun prefHelper(context: Context) : PrefHelper {
        PrefHelper.setSharedPreferences(context, ConstValue.sharedPreferencesName, Context.MODE_PRIVATE)
        return PrefHelper()
    }

}