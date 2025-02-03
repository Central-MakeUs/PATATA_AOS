package com.cmc.data.auth.repository

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cmc.data.auth.TokenStorage
import com.cmc.data.auth.model.LoginRequest
import com.cmc.data.auth.model.toDomain
import com.cmc.data.auth.remote.AuthApiService
import com.cmc.data.base.apiRequestCatching
import com.cmc.data.base.asFlow
import com.cmc.data.di.GeneralApi
import com.cmc.domain.auth.model.AuthResponse
import com.cmc.domain.auth.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject

val Context.userDataStore by preferencesDataStore(name = "user_data")

internal class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @GeneralApi private val authApiService: AuthApiService,
    private val tokenStorage: TokenStorage,
): AuthRepository {

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("key_user_id")
    }

    private val secretKey: SecretKey = generateSecretKey()

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
        tokenStorage.saveTokens(accessToken, refreshToken)
    }

    override suspend fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }

    override suspend fun clearTokens() {
        tokenStorage.clearTokens()
    }

    override suspend fun saveUserId(userId: String) {
        val encryptedUserId = encrypt(userId, secretKey)
        context.userDataStore.edit { preferences ->
            preferences[KEY_USER_ID] = encryptedUserId
        }
    }

    override suspend fun getUserId(): String? {
        return context.userDataStore.data.map { preferences ->
            preferences[KEY_USER_ID]?.let { encryptUserId ->
                decrypt(encryptUserId, secretKey)
            }
        }.first()
    }

    private fun generateSecretKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256) // AES-256
        return keyGen.generateKey()
    }

    // 암호화
    private fun encrypt(data: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    // 복호화
    private fun decrypt(data: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(data, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes)
    }
}