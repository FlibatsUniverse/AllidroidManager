package com.flibatsuniverse.allidroid.manager.di

import com.flibatsuniverse.allidroid.manager.domain.repository.RestRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::RestRepository)
}