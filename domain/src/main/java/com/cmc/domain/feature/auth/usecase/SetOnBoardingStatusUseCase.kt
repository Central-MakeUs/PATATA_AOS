package com.cmc.domain.feature.auth.usecase

import com.cmc.domain.feature.auth.repository.AuthRepository
import javax.inject.Inject

class SetOnBoardingStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(isComplete: Boolean): Result<Boolean> {
        return authRepository.setOnboardingStatus(isComplete)
    }
}