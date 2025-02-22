package com.cmc.domain.feature.auth.usecase

import com.cmc.domain.feature.auth.repository.AuthRepository
import com.cmc.domain.model.ImageMetadata
import javax.inject.Inject

class UpdateProfileImageUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(profileImage: ImageMetadata): Result<String> {
        return authRepository.updateProfileImage(profileImage)
    }
}