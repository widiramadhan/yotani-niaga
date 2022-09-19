package com.javaindoku.yotaniniaga

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen
import com.javaindoku.yotaniniaga.di.component.AppComponent
import com.javaindoku.yotaniniaga.di.component.DaggerAppComponent
import com.javaindoku.yotaniniaga.utilities.PrefHelper
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.zeugmasolutions.localehelper.LocaleHelperApplicationDelegate
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.*
import javax.inject.Inject


class MainApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispactchingActivityInjector: DispatchingAndroidInjector<Any>

    private val localeAppDelegate =
        LocaleHelperApplicationDelegate()

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
//        PrefHelper.setSharedPreferences(applicationContext, GlobalVar.sharedPreferencesName,
//            Context.MODE_PRIVATE)

    }

    override fun androidInjector(): AndroidInjector<Any> = dispactchingActivityInjector

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(localeAppDelegate.attachBaseContext(base!!))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localeAppDelegate.onConfigurationChanged(this)
    }
}