package com.cmc.data.di

import com.cmc.data.auth.TokenStorage
import com.cmc.data.auth.remote.AuthApiService
import com.cmc.data.base.ApiConfig
import com.cmc.data.base.ApiConfig.BASE_URL
import com.cmc.data.base.TokenApiService
import com.cmc.data.base.TokenDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeneralApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TokenRefreshApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 1. 일반 API 요청용 OkHttpClient
    @GeneralApi
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)   // 액세스 토큰 추가
            .authenticator(tokenAuthenticator) // 토큰 만료 시 갱신
            .build()
    }

    // 2. 일반 API 요청용 Retrofit
    @GeneralApi
    @Provides
    @Singleton
    fun provideRetrofit(@GeneralApi okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 3. 토큰 갱신 전용 OkHttpClient (인터셉터 제거)
    @TokenRefreshApi
    @Provides
    @Singleton
    fun provideTokenRefreshOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()  // 인증 관련 인터셉터 제거
    }

    // 4. 토큰 갱신 전용 Retrofit
    @TokenRefreshApi
    @Provides
    @Singleton
    fun provideTokenRefreshRetrofit(@TokenRefreshApi tokenRefreshOkHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(tokenRefreshOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
