package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class DeleteReviewUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(reviewId: Int): Result<Unit> {
        return spotRepository.deleteReview(reviewId)
    }
}