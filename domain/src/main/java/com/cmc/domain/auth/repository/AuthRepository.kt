package com.cmc.domain.auth.repository

import com.cmc.domain.auth.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun login(idToken: String): Flow<Result<User>>

}