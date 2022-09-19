package com.javaindoku.yotaniniaga.base.`interface`

import dagger.Module

interface ToolbarInterface {
    fun initToolbar()
    fun newNotification(totalNotification: Int)
    fun clearNotification()
}