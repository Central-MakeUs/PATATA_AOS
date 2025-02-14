package com.cmc.domain.feature.auth.usecase

import com.cmc.domain.feature.auth.model.AuthResponse
import com.cmc.domain.feature.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
)  {
    suspend operator fun invoke(idToken: String): Flow<Result<AuthResponse>> {
        return authRepository.login(idToken)
    }
}