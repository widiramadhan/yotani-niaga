package com.javaindoku.yotaniniaga.utilities.extension

import android.os.SystemClock
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

fun View.setOnSafeClickListener(listener: (view: View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000)
            return@setOnClickListener
        lastClickTime = SystemClock.elapsedRealtime()
        listener.invoke(it)
    }
}

fun Fragment.findNavController(): NavController =
    NavHostFragment.findNavController(this)