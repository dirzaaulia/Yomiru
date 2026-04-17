package com.dirzaaulia.yomiru.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.min

object ApiRateLimiter {
    private val mutex = Mutex()

    private var lastRefillTime = System.currentTimeMillis()
    private var tokens = MAX_TOKENS

    private const val MAX_TOKENS = 3         // e.g. 2 requests
    private const val REFILL_INTERVAL_MS = 2000L // per second

    suspend fun <T> run(block: suspend () -> T): T {
        return mutex.withLock {
            refillTokens()

            if (tokens == 0) {
                // no tokens, must wait
                val waitTime = REFILL_INTERVAL_MS - (System.currentTimeMillis() - lastRefillTime)
                if (waitTime > 0) delay(waitTime)
                refillTokens()
            }

            tokens--
            block()
        }
    }

    private fun refillTokens() {
        val now = System.currentTimeMillis()
        val elapsed = now - lastRefillTime

        if (elapsed >= REFILL_INTERVAL_MS) {
            val newTokens = (elapsed / REFILL_INTERVAL_MS).toInt()
            tokens = min(MAX_TOKENS, tokens + newTokens)
            lastRefillTime = now
        }
    }
}