package com.cmc.data.di

import com.cmc.data.feature.token.repository.TokenRepository
import com.cmc.data.feature.auth.repository.AuthRepositoryImpl
import com.cmc.data.feature.location.LocationRepositoryImpl
import com.cmc.data.feature.report.repository.ReportRepositoryImpl
import com.cmc.data.feature.spot.repository.SpotRepositoryImpl
import com.cmc.data.feature.token.repository.TokenRepositoryImpl
import com.cmc.domain.feature.auth.repository.AuthRepository
import com.cmc.domain.feature.location.LocationRepository
import com.cmc.domain.feature.report.repository.ReportRepository
import com.cmc.domain.feature.spot.repository.SpotRepository
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
    fun provideTokenRepositoryImpl(tokenRepository: TokenRepositoryImpl): TokenRepository

    @Binds
    fun provideSpotRepositoryImpl(spotRepository: SpotRepositoryImpl): SpotRepository

    @Binds
    fun provideReportRepositoryImpl(reportRepository: ReportRepositoryImpl): ReportRepository
}