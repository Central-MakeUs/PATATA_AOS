package com.cmc.domain.feature.auth.usecase

import com.cmc.domain.feature.auth.repository.AuthRepository
import javax.inject.Inject

class ClearTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        return authRepository.clearTokens()
    }
}