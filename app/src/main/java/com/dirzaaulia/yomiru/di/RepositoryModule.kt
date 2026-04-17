package com.dirzaaulia.yomiru.di

import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.repository.NetworkRepositoryImpl
import id.pgidata.gomamam.repository.DataStoreRepository
import io.ktor.client.HttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<NetworkRepository> { // Explicitly define the interface type
        provideNetworkRepository(
            jikanClient = get(named(KtorClientQualifiers.JIKAN_HTTP_CLIENT)),
            malAuthClient = get(named(KtorClientQualifiers.MAL_AUTH_HTTP_CLIENT)),
            malClient = get(named(KtorClientQualifiers.MAL_API_HTTP_CLIENT))
        )
    }
}

private fun provideNetworkRepository(
    jikanClient: HttpClient,
    malAuthClient: HttpClient,
    malClient: HttpClient
): NetworkRepository {
    return NetworkRepositoryImpl(
        jikanClient = jikanClient,
        malAuthClient = malAuthClient,
        malClient = malClient
    )
}