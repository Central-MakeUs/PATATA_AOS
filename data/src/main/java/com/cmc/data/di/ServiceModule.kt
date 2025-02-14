package com.cmc.data.di

import com.cmc.data.base.TokenApiService
import com.cmc.data.feature.auth.remote.AuthApiService
import com.cmc.data.feature.spot.remote.SpotApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object ServiceModule {

    // 토큰 갱신 처리용
    @TokenRefreshApi
    @Provides
    @Singleton
    fun provideTokenApiService(@TokenRefreshApi tokenRefreshRetrofit: Retrofit): TokenApiService {
        return tokenRefreshRetrofit.create(TokenApiService::class.java)
    }


    @Provides
    @Singleton
    fun provideAuthApiService( retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSpotApiService(retrofit: Retrofit): SpotApiService {
        return retrofit.create(SpotApiService::class.java)
    }
}