package com.javaindoku.yotaniniaga.view.splashscreen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.utilities.PrefHelper
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.view.editprofilepetani.EditProfilePetaniActivity
import com.javaindoku.yotaniniaga.view.login.LoginActivity
import com.javaindoku.yotaniniaga.view.mainkoperasi.MainKoperasiActivity
import dagger.android.AndroidInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        checkLanguageIndonesia()

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FirebaseInstanceId", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result!!.token
            prefHelper.setStringToShared(SharedPrefKeys.FIREBASE_TOKEN, token)

        })

        GlobalScope.launch(context = Dispatchers.Main) {
            delay(1000)

            val isLogin = prefHelper.getBoolFromShared(SharedPrefKeys.IS_LOGIN)
            if (isLogin) {
                val borrowedId = prefHelper.getStringFromShared(SharedPrefKeys.BORROWER_ID).toString()
                val typeUser = prefHelper.getStringFromShared(SharedPrefKeys.TYPE_USER).toString()
                if ((borrowedId == "" || borrowedId == "null") && typeUser == "PTN") {
                    val intent = Intent(applicationContext, EditProfilePetaniActivity::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(applicationContext, MainKoperasiActivity::class.java)
                    startActivity(intent)
                }
            } else {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }

    private fun checkLanguageIndonesia() {
        val languageLocal = getLocale()!!
        if (languageLocal.language != "in") {
            setDefaultLanguageToIndonesia()
        }
    }
}