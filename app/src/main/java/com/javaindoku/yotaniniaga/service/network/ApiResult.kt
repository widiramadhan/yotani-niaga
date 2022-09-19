package com.javaindoku.yotaniniaga.service.network

data class ApiResult<out T>(val status: Status, val data: T?=null, val message: String?, var messageInt: Int? = 0) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T): ApiResult<T> {
            return ApiResult(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String): ApiResult<T> {
            return ApiResult(Status.ERROR, data = null, message = message)
        }

        fun <T> loading(data: T? = null): ApiResult<T> {
            return ApiResult(Status.LOADING, data, "")
        }

        fun <T> errorInt(messageInt: Int, data: T? = null): ApiResult<T> {
            return ApiResult(Status.ERROR, data, "", messageInt)
        }

    }
}