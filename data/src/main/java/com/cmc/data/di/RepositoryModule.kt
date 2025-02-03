package com.cmc.data.di

import com.cmc.data.auth.repository.AuthRepositoryImpl
import com.cmc.data.base.TokenDataSource
import com.cmc.data.base.TokenDataSourceImpl
import com.cmc.data.location.LocationRepositoryImpl
import com.cmc.domain.auth.repository.AuthRepository
import com.cmc.domain.location.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    fun provideAuthRepositoryImpl(authRepository: AuthRepositoryImpl): AuthRepository

    @Binds
    fun provideLocationRepositoryImpl(locationRepository: LocationRepositoryImpl): LocationRepository

    @Binds
    fun provideTokenDataSourceImpl(tokenDataSource: TokenDataSourceImpl): TokenDataSource

}