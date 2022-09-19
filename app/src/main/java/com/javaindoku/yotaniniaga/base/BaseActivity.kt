package com.javaindoku.yotaniniaga.base

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.javaindoku.yotaniniaga.utilities.PrefHelper
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegate
import com.zeugmasolutions.localehelper.LocaleHelperActivityDelegateImpl
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import java.util.*
import javax.inject.Inject

open class BaseActivity : AppCompatActivity(), HasAndroidInjector {

    fun loadingDialog(activity: Activity) = CustomDialog.showLoadingDialog(activity, true)

    @Inject
    lateinit var dispactchingActivityInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var prefHelper: PrefHelper

    override fun androidInjector(): AndroidInjector<Any> = dispactchingActivityInjector

    private val localeDelegate: LocaleHelperActivityDelegate = LocaleHelperActivityDelegateImpl()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(localeDelegate.attachBaseContext(newBase!!))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(
            localeDelegate.applyOverrideConfiguration(baseContext, overrideConfiguration)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localeDelegate.onCreate(this)
        //default language indonesia
//        localeDelegate.setLocale(this, Locale("in"))
    }

    override fun onResume() {
        super.onResume()
        localeDelegate.onResumed(this)
    }

    override fun onPause() {
        super.onPause()
        localeDelegate.onPaused()
    }

    fun updateLocale() {
        val languageLocal = getLocale()!!
        if(languageLocal.language.equals("in"))
        {
            localeDelegate.setLocale(this, Locale("en"))
            prefHelper.setStringToShared(SharedPrefKeys.LANGUAGE, "en")
        }
        else
        {
            localeDelegate.setLocale(this, Locale("in"))
            prefHelper.setStringToShared(SharedPrefKeys.LANGUAGE, "in")
        }

    }

    fun setDefaultLanguageToIndonesia() {
        localeDelegate.setLocale(this, Locale("in"))
        prefHelper.setStringToShared(SharedPrefKeys.LANGUAGE, "in")
    }

    fun getLocale(): Locale? {
        var lang = prefHelper.getStringFromShared(SharedPrefKeys.LANGUAGE)
        lang = when (lang) {
            "" -> ""
            "en" -> "en"
            else -> "in"
        }
        return Locale(lang)
    }
}