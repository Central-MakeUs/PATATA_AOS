package com.cmc.domain.auth.usecase

import com.cmc.domain.auth.model.User
import com.cmc.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
)  {
    suspend operator fun invoke(idToken: String): Flow<Result<User>> {
        return authRepository.login(idToken)
    }
}