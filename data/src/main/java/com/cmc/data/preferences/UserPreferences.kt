package com.cmc.data.preferences

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.cmc.data.base.constants.DataStoreKeys.KEY_USER_ID
import com.cmc.data.base.constants.DataStoreKeys.USER_PREFERENCES
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject

val Context.userPreferences by preferencesDataStore(USER_PREFERENCES)

class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val secretKey: SecretKey = generateSecretKey()

    suspend fun saveUserId(userId: String) {
        val encryptedUserId = encrypt(userId, secretKey)
        context.userPreferences.edit { preferences ->
            preferences[KEY_USER_ID] = encryptedUserId
        }
    }

    suspend fun getUserId(): String? {
        return context.userPreferences.data.map { preferences ->
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