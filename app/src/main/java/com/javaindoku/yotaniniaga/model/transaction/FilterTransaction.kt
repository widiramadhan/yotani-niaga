package com.javaindoku.yotaniniaga.model.transaction

data class FilterTransaction (
    var id: String = "",
    var name: String = "",
    var distance: String = ""
) {
    override fun toString(): String {
        return name
    }
}