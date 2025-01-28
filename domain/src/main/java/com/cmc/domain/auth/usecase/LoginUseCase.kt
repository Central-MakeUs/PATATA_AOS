package com.cmc.domain.auth.usecase

import com.cmc.domain.auth.model.AuthResponse
import com.cmc.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
)  {
    suspend operator fun invoke(idToken: String): Flow<Result<AuthResponse>> {
        return authRepository.login(idToken)
    }
}