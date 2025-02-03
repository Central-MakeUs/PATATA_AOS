package com.cmc.data.di

import com.cmc.data.auth.remote.AuthApiService
import com.cmc.data.base.TokenApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object ServiceModule {
    // 5. 일반 API 서비스
    @GeneralApi
    @Provides
    @Singleton
    fun provideApiService(@GeneralApi retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    // 6. 토큰 갱신용 API 서비스
    @TokenRefreshApi
    @Provides
    @Singleton
    fun provideTokenApiService(@TokenRefreshApi tokenRefreshRetrofit: Retrofit): TokenApiService {
        return tokenRefreshRetrofit.create(TokenApiService::class.java)
    }
}