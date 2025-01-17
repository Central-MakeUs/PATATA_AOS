package com.cmc.data.di

import com.cmc.data.auth.repository.AuthRepositoryImpl
import com.cmc.domain.auth.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    fun provideRepositoryImpl(authRepository: AuthRepositoryImpl): AuthRepository

}