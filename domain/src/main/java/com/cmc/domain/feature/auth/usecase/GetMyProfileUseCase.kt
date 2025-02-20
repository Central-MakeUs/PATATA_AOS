package com.cmc.domain.feature.auth.usecase

import com.cmc.domain.feature.auth.model.Member
import com.cmc.domain.feature.auth.repository.AuthRepository
import javax.inject.Inject

class GetMyProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Member> {
        return authRepository.getMyProfile()
    }
}