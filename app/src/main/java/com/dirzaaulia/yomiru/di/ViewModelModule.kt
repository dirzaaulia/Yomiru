package com.dirzaaulia.yomiru.di

import com.dirzaaulia.yomiru.AuthCallbackViewModel
import com.dirzaaulia.yomiru.screen.detail.DetailViewModel
import com.dirzaaulia.yomiru.screen.home.HomeViewModel
import com.dirzaaulia.yomiru.screen.list.ListViewModel
import com.dirzaaulia.yomiru.screen.search.SearchViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::ListViewModel)
    singleOf(::AuthCallbackViewModel)
}