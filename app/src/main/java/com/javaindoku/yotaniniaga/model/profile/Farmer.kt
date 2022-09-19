package com.javaindoku.yotaniniaga.model.profile

import com.javaindoku.yotaniniaga.utilities.AdapterDisplayText

data class Farmer (
    var id: String = "",
    var name: String = "",
    var phoneNo: String = "",
    var picture: String = ""
) : AdapterDisplayText {
    override fun displayName(): String {
        return name
    }

    override fun selectId(): Any {
        return id
    }
}