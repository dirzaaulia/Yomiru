package com.dirzaaulia.yomiru.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.dirzaaulia.yomiru.BuildConfig
// Ensure DataStoreRepository is correctly imported if its package is different
import com.dirzaaulia.yomiru.model.response.MalTokenResponse // For refresh token response
import id.pgidata.gomamam.repository.DataStoreRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth // Ensure this import is present
import io.ktor.client.plugins.auth.AuthConfig
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
// Removed Response import from okhttp3 as it's not directly used here anymore
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

object KtorClientQualifiers {
    const val JIKAN_HTTP_CLIENT = "JikanHttpClient"
    const val MAL_AUTH_HTTP_CLIENT = "MalAuthHttpClient"
    const val MAL_API_HTTP_CLIENT = "MalApiHttpClient"
}

private fun createConfiguredHttpClient(
    context: Context,
    host: String,
    protocol: URLProtocol = URLProtocol.HTTPS,
    customEngineInterceptors: List<Interceptor> = emptyList(),
    authPluginConfig: (AuthConfig.() -> Unit)? = null // Type for Ktor Auth plugin
): HttpClient {
    return HttpClient(OkHttp) {
        expectSuccess = true
        engine {
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
        authPluginConfig?.let { config ->
            install(Auth, config)
        }
        defaultRequest {
            url {
                this.protocol = protocol
                this.host = host
            }
        }
    }
}

val networkModule = module {

    // Provides DataStoreRepository
    // If you have this in another module (e.g., dataModule), you can remove this definition.
    single { DataStoreRepository(context = get()) }

    single(named(KtorClientQualifiers.JIKAN_HTTP_CLIENT)) {
        createConfiguredHttpClient(
            context = get(),
            host = "api.jikan.moe/v4"
        )
    }

    single(named(KtorClientQualifiers.MAL_AUTH_HTTP_CLIENT)) {
        createConfiguredHttpClient(
            context = get(),
            host = "myanimelist.net/v1/oauth2",
//            // No specific authPluginConfig here if this client is only for refresh and the refresh itself uses basic auth on the request.
//            // If the MAL_AUTH_HTTP_CLIENT is also used for the initial PKCE token request (which needs Basic Auth for the client itself):
//            authPluginConfig = {
//                basic {
//                    credentials {
//                        BasicAuthCredentials(
//                            username = BuildConfig.MAL_CLIENT_ID,
//                            password = "" // Assuming empty client_secret
//                        )
//                    }
//                    sendWithoutRequest { request ->
//                        // Only apply Basic Auth proactively to the token endpoint.
//                        // If the request is NOT to the token endpoint, sendWithoutRequest is true (skip basic auth).
//                        // Otherwise (it IS the token endpoint), sendWithoutRequest is false (apply basic auth).
//                        !(request.url.host == "myanimelist.net" && request.url.pathSegments.containsAll(listOf("v1", "oauth2", "token")))
//                    }
//                }
//            }
        )
    }

    single(named(KtorClientQualifiers.MAL_API_HTTP_CLIENT)) {
        val dataStoreRepository = get<DataStoreRepository>()
        val malAuthClient = get<HttpClient>(named(KtorClientQualifiers.MAL_AUTH_HTTP_CLIENT))

        createConfiguredHttpClient(
            context = get(),
            host = "api.myanimelist.net/v2",
            authPluginConfig = {
                bearer {
                    loadTokens {
                        val accessToken = dataStoreRepository.accessTokenFlow.first()
                        val refreshToken = dataStoreRepository.refreshTokenFlow.first()
                        if (accessToken.isNotBlank()) {
                            BearerTokens(accessToken, refreshToken)
                        } else {
                            null
                        }
                    }
                    refreshTokens {
                        // oldTokens is the BearerTokens from loadTokens or previous refresh
                        val currentRefreshToken = oldTokens?.refreshToken ?: dataStoreRepository.refreshTokenFlow.first()
                        if (currentRefreshToken.isBlank()) {
                            // Cannot refresh without a refresh token
                            return@refreshTokens null
                        }

                        val response: MalTokenResponse = malAuthClient.post("https://myanimelist.net/v1/oauth2/token") {
                            setBody(FormDataContent(Parameters.build {
                                append("grant_type", "refresh_token")
                                append("refresh_token", currentRefreshToken)
                                append("client_id", BuildConfig.MAL_CLIENT_ID)
                                // MAL's refresh token flow with a public client might not need client_secret or PKCE.
                                // Basic Auth (client_id as username) for the *client itself* during token refresh
                                // is handled by the malAuthClient's own basic auth configuration if it's set up for proactive auth.
                                // If malAuthClient is NOT set for proactive basic auth on the refresh token call, then the server must issue a 401 challenge for it.
                            }))
                        }.body()

                        dataStoreRepository.setAccessToken(response.accessToken)
                        dataStoreRepository.setRefreshTokenToken(response.refreshToken)
                        // If expires_in is relevant, save it too:
                        // dataStoreRepository.setExpiresIn(response.expires_in)
                        BearerTokens(response.accessToken, response.refreshToken)
                    }
                    sendWithoutRequest { request ->
                        // If the request's host is NOT api.myanimelist.net, then send this request WITHOUT the Bearer token.
                        // Otherwise (if host IS api.myanimelist.net), the condition is false, and Ktor WILL add the Bearer token.
                        request.url.host != "api.myanimelist.net"
                    }
                }
            }
        )
    }
}