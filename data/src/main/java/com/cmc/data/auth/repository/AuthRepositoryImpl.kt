package com.cmc.data.auth.repository

import com.cmc.data.auth.model.LoginRequest
import com.cmc.data.auth.model.toDomain
import com.cmc.data.auth.remote.AuthApiService
import com.cmc.data.base.apiRequestCatching
import com.cmc.data.base.asFlow
import com.cmc.domain.auth.model.User
import com.cmc.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
): AuthRepository {

    override suspend fun login(idToken: String): Flow<Result<User>> {
        return apiRequestCatching(
            apiCall = { authApiService.googleLogin(LoginRequest(idToken))},
            transform = { it.toDomain() }
        ).asFlow()
    }
}