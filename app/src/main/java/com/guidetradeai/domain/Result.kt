package com.guidetradeai.domain

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun <T> error(message: String, code: String? = null): Result<T> = Error(message, code)
        fun <T> loading(): Result<T> = Loading
    }
}

fun <T> Result<T>.messageOrNull(): String? = (this as? Result.Error)?.message
