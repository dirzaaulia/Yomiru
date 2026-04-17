package com.dirzaaulia.yomiru.util

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.channels.UnresolvedAddressException

/**
 * A generic class that holds a value or an exception
 */
sealed class ResponseResult<out R> {
    data object Loading: ResponseResult<Nothing>()
    data class Success<out T>(val data: T) : ResponseResult<T>()
    data class Error(val throwable: Throwable) : ResponseResult<Nothing>()
}

/**
 * Helper to extract clean error messages from Ktor and System exceptions
 */
fun Throwable.toHumanReadableError(): String {
    return when (this) {
        is ClientRequestException -> {
            val code = this.response.status.value
            if (code == 429) "Too many requests. Please wait a moment."
            else "Client Error $code: ${this.message}"
        }
        is ResponseException -> {
            "Server Error ${this.response.status.value}"
        }
        is UnresolvedAddressException, is IOException -> {
            "No internet connection. Please check your network."
        }
        else -> this.localizedMessage ?: "An unknown error occurred"
    }
}

inline fun <T> executeWithData(body: () -> T): ResponseResult<T> {
    return try {
        ResponseResult.Success(body.invoke())
    } catch (e: Exception) {
        e.printStackTrace()
        ResponseResult.Error(e)
    }
}

/**
 * Wraps safeIoCall with a loading state emission for Flows
 */
fun <T> safeFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: suspend () -> T
): Flow<ResponseResult<T>> = flow {
    emit(ResponseResult.Loading)
    emit(safeIoCall { block() })
}.flowOn(dispatcher)

/**
 * Handles the actual IO call with built-in retry logic for 429 errors
 */
suspend inline fun <T> safeIoCall(
    retries: Int = 3,
    delayMillis: Long = 3000L,
    crossinline block: suspend () -> T
): ResponseResult<T> {
    var currentAttempt = 0

    while (true) {
        try {
            // Respect the local rate limiter first
            val data = withContext(Dispatchers.IO) {
                ApiRateLimiter.run { block() }
            }
            return ResponseResult.Success(data)
        } catch (e: Exception) {
            // Check if it's a Ktor ClientRequestException (4xx) and status is 429
            val isRateLimit = e is ClientRequestException &&
                    e.response.status == HttpStatusCode.TooManyRequests

            if (isRateLimit && currentAttempt < retries) {
                currentAttempt++
                // Log for debugging: println("Rate limited, retrying in ${delayMillis}ms... (Attempt $currentAttempt)")
                delay(delayMillis)
                continue // Re-run the 'try' block
            }

            // If it's not a 429, or we've run out of retries, return the error
            return ResponseResult.Error(e)
        }
    }
}