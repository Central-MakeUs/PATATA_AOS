package com.cmc.domain.feature.auth.usecase

import com.cmc.domain.feature.auth.repository.AuthRepository
import javax.inject.Inject

class SaveTokensUseCase @Inject constructor(
    private val authRepository: AuthRepository
)  {
    suspend operator fun invoke(accessToken: String, refreshToken: String): Unit {
        return authRepository.saveTokens(accessToken, refreshToken)
    }
}