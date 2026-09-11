package com.guidetradeai.domain

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun error(message: String): Result<Nothing> = Error(message)
        fun loading(): Result<Nothing> = Loading
    }

    fun messageOrNull(): String? = if (this is Error) message else null
    fun getOrNull(): T? = if (this is Success) data else null
    fun isError(): Boolean = this is Error
    fun isSuccess(): Boolean = this is Success
    fun isLoading(): Boolean = this is Loading
}
