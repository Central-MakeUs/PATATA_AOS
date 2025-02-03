package com.cmc.data.di


import com.cmc.data.base.ApiConfig.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TokenRefreshApi

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    // 토큰 갱신 처리용 Client 및 Retrofit
    @TokenRefreshApi
    @Provides
    @Singleton
    fun provideTokenRefreshOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()  // 인증 관련 인터셉터 제거
    }

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

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
