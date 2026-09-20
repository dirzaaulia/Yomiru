package com.dirzaaulia.yomiru.di

import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerInterceptor
import id.pgidata.gomamam.repository.DataStoreRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

object KtorClientQualifiers {
    const val ANILIST_HTTP_CLIENT = "AniListHttpClient"
}

class AniListAuthInterceptor(
    private val dataStore: DataStoreRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = runBlocking { dataStore.accessTokenFlow.first() }
        val builder = originalRequest.newBuilder()
        if (token.isNotBlank()) {
            builder.header("Authorization", "Bearer $token")
        }
        return chain.proceed(builder.build())
    }
}

class AniListGraphQLInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body

        if (requestBody != null && request.url.host == "graphql.anilist.co") {
            try {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                val rawBody = buffer.readUtf8()
                val formattedBody = rawBody.replace("\\n", "\n").replace("\\\"", "\"")

                Log.d("GraphQL", "==================================================")
                Log.d("GraphQL", "🚀 GRAPHQL REQUEST -> ${request.url}")
                Log.d("GraphQL", formattedBody)
                Log.d("GraphQL", "==================================================")
            } catch (e: Exception) {
                Log.e("GraphQL", "Error reading GraphQL request body", e)
            }
        }

        val startTime = System.currentTimeMillis()
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - startTime

        Log.d("GraphQL", "✅ GRAPHQL RESPONSE [${response.code}] (${duration}ms) for ${request.url}")
        return response
    }
}

private fun createAniListHttpClient(
    context: Context,
    dataStore: DataStoreRepository,
    customEngineInterceptors: List<Interceptor> = emptyList()
): HttpClient {
    return HttpClient(OkHttp) {
        expectSuccess = true
        engine {
            addInterceptor(AniListAuthInterceptor(dataStore))
            addInterceptor(AniListGraphQLInterceptor())
            addInterceptor(ChuckerInterceptor(context))
            customEngineInterceptors.forEach { addInterceptor(it) }
            config {
                connectTimeout(timeout = 60L, unit = TimeUnit.SECONDS)
                writeTimeout(timeout = 60L, unit = TimeUnit.SECONDS)
                readTimeout(timeout = 60L, unit = TimeUnit.SECONDS)
            }
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
        install(Resources)
        defaultRequest {
            url {
                this.protocol = URLProtocol.HTTPS
                this.host = "graphql.anilist.co"
            }
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Accept, ContentType.Application.Json)
        }
    }
}

val networkModule = module {

    single { DataStoreRepository(context = get()) }

    single(named(KtorClientQualifiers.ANILIST_HTTP_CLIENT)) {
        createAniListHttpClient(
            context = get(),
            dataStore = get()
        )
    }
}
