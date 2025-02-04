package com.cmc.domain.feature.auth.usecase

import com.cmc.domain.feature.auth.repository.AuthRepository
import javax.inject.Inject

class GetOnboardingStatus @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return authRepository.getOnboardingStatus()
    }
}