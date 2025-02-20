package com.cmc.data.feature.auth.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cmc.data.base.apiRequestCatching
import com.cmc.data.base.asFlow
import com.cmc.data.base.uriToFile
import com.cmc.data.feature.auth.model.LoginRequest
import com.cmc.data.feature.auth.model.NickNameRequest
import com.cmc.data.feature.auth.model.toDomain
import com.cmc.data.feature.auth.remote.AuthApiService
import com.cmc.data.preferences.AppPreferences
import com.cmc.data.preferences.TokenPreferences
import com.cmc.data.preferences.UserPreferences
import com.cmc.domain.base.exception.ApiException
import com.cmc.domain.feature.auth.model.AuthResponse
import com.cmc.domain.feature.auth.model.Member
import com.cmc.domain.feature.auth.repository.AuthRepository
import com.cmc.domain.model.ImageMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


internal class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authApiService: AuthApiService,
    private val tokenPreferences: TokenPreferences,
    private val userPreferences: UserPreferences,
    private val appPreferences: AppPreferences,
): AuthRepository {

    override suspend fun getOnboardingStatus(): Boolean {
        return  kotlin.runCatching {
            appPreferences.getOnboardingStatus()
        }.getOrElse {
            false
        }
    }

    override suspend fun setOnboardingStatus(isComplete: Boolean): Result<Boolean> {
        return kotlin.runCatching {
            appPreferences.setOnboardingStatus(isComplete)
            true
        }
    }

    override suspend fun login(idToken: String): Flow<Result<AuthResponse>> {
        return apiRequestCatching(
            apiCall = { authApiService.googleLogin(LoginRequest(idToken))},
            transform = { it.toDomain() },
            successCallBack = { result ->
                saveTokens(result.accessToken, result.refreshToken)
            }
        ).asFlow()
    }

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenPreferences.saveTokens(accessToken, refreshToken)
    }

    override suspend fun getAccessToken(): String? {
        return tokenPreferences.getAccessToken()
    }

    override suspend fun getRefreshToken(): String? {
        return tokenPreferences.getRefreshToken()
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        return try {
            val response = authApiService.refreshAccessToken()

            if (response.isSuccess && response.result != null) {
                val newAccessToken = response.result.accessToken
                val newRefreshToken = response.result.refreshToken
                tokenPreferences.saveTokens(newAccessToken, newRefreshToken)
                Result.success(Unit)
            } else {
                Result.failure(ApiException.TokenExpired("토큰이 만료되었습니다."))
            }
        } catch (e: Exception) {
            Result.failure(ApiException.ServerError("서버 오류 발생: ${e.message}"))
        }
    }

    override suspend fun clearTokens() {
        tokenPreferences.clearTokens()
    }

    override suspend fun saveUserId(userId: String) {
        userPreferences.saveUserId(userId)
    }

    override suspend fun getUserId(): String? {
        return userPreferences.getUserId()
    }

    override suspend fun updateNickName(nickName: String): Result<Unit> {
        return apiRequestCatching(
            apiCall = { authApiService.updateNickName(
                NickNameRequest(nickName)
            )},
            responseClass = Unit::class,
        )
    }

    override suspend fun updateProfileImage(profileImage: ImageMetadata): Result<String> {
        return apiRequestCatching(
            apiCall = {
                val file = uriToFile(context, Uri.parse(profileImage.uri), profileImage.fileName)
                val imageFilePart = createImageMultipart(file, profileImage.fileName, profileImage.mimeType)

                authApiService.updateProfileImage(imageFilePart)
            },
            transform = { it }
        )
    }
    private fun createImageMultipart(file: File, fileName: String, mimeType: String?): MultipartBody.Part {
        val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull() ?: "image/jpeg".toMediaTypeOrNull())
        val imageFilePart = MultipartBody.Part.createFormData(
            "profileImage",
            fileName,
            requestFile
        )

        return imageFilePart
    }

    override suspend fun getMyProfile(): Result<Member> {
        return apiRequestCatching(
            apiCall = { authApiService.getMyProfile() },
            transform = { it.toDomain() }
        )
    }
}