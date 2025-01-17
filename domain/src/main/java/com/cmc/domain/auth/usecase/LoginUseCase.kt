package com.cmc.domain.auth.usecase

import com.cmc.domain.auth.model.User
import com.cmc.domain.auth.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
)  {
    suspend operator fun invoke(idToken: String): User {
        return authRepository.login(idToken)
    }
}