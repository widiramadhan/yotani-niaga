package com.javaindoku.yotaniniaga.model.invoice

/*
*
*
*@Author
*@Version
*/
data class InvoiceRequest (
    var user_id: String = "",
    var keyword: String = "",
    var sort: String = "",
    var page: String = "",
    var filter: RequestFilter? = RequestFilter()
)
