package com.example.medicinereminder.data.repository

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    companion object {
        fun <T> loading() = Loading
        fun <T> success(data: T) = Success(data)
        fun <T> error(exception: Exception) = Error(exception)
    }
}