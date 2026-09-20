package com.dirzaaulia.yomiru.di

import com.dirzaaulia.yomiru.repository.NetworkRepository
import com.dirzaaulia.yomiru.repository.NetworkRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single<NetworkRepository> {
        NetworkRepositoryImpl(
            client = get(named(KtorClientQualifiers.ANILIST_HTTP_CLIENT))
        )
    }
}
