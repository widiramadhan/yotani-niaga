package com.javaindoku.yotaniniaga.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.InspectableProperty
import androidx.annotation.InspectableProperty.EnumEntry
import com.javaindoku.yotaniniaga.BuildConfig

class PrefHelper {
    companion object{
        private lateinit var sharedPreferences: SharedPreferences
        fun setSharedPreferences(context: Context, nameSharedPreferences: String, modeType: Int) {
            sharedPreferences = context.getSharedPreferences(nameSharedPreferences, modeType);
        }
    }

    fun setIntToShared(enumEntry: SharedPrefKeys, value: Int) {
        try {
            sharedPreferences.edit().putInt(enumEntry.name, value).apply()
        } catch (exception: ExceptionInInitializerError) {
            exception.printStackTrace()
        }
    }

    fun getIntFromShared(enumEntry: SharedPrefKeys): Int {
        return try {
            sharedPreferences.getInt(enumEntry.name, 0)
        } catch (exception: ExceptionInInitializerError) {
            exception.printStackTrace()
            0
        }
    }

    fun setStringToShared(enumEntry: SharedPrefKeys, value: String) {
        try {
            if(BuildConfig.DEBUG) {
                sharedPreferences.edit().putString(enumEntry.name, value).apply()
            }
            else
            {
                sharedPreferences.edit().putString(enumEntry.name, value.encrypt()).apply()
            }

        } catch (exception: ExceptionInInitializerError) {
            exception.printStackTrace()
        }
    }

    fun getStringFromShared(enumEntry: SharedPrefKeys): String? {
        return try {
            if(BuildConfig.DEBUG) {
                sharedPreferences.getString(enumEntry.name, "")
            }
            else
            {
                if(sharedPreferences.getString(enumEntry.name, "") == null) null else sharedPreferences.getString(enumEntry.name, "")!!.decrypt()
            }
        } catch (exception: ExceptionInInitializerError) {
            exception.printStackTrace()
            "null"
        }
    }

    fun setBooleanToShared(enumEntry: SharedPrefKeys, value: Boolean) {
        try {
            sharedPreferences.edit().putBoolean(enumEntry.name, value).apply()
        } catch (exception: ExceptionInInitializerError) {
            exception.printStackTrace()
        }
    }

    fun getBoolFromShared(enumEntry: SharedPrefKeys): Boolean {
        return try {
            sharedPreferences.getBoolean(enumEntry.name, false)
        } catch (exception: ExceptionInInitializerError) {
            exception.printStackTrace()
            false
        }
    }

    fun clearDataLogout(){
        sharedPreferences.edit().clear().apply()
    }
}