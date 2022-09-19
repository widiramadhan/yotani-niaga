package com.javaindoku.yotaniniaga.utilities.extension

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

fun Any.toJson(): String = GsonBuilder().serializeNulls().create().toJson(this)

fun <T> String.fromJson(): T = GsonBuilder().serializeNulls().create().fromJson(this, getType<T>())

fun <T> Any.getType(): Type = object : TypeToken<T>() {}.type