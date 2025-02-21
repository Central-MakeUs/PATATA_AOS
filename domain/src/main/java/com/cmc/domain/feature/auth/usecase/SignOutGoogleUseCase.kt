package com.cmc.domain.feature.auth.usecase

import com.cmc.domain.feature.auth.repository.AuthRepository
import javax.inject.Inject

class SignOutGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(googleAccessToken: String): Result<String> {
        return authRepository.signOutGoogle(googleAccessToken)
    }
}