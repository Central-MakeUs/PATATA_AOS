package com.cmc.domain.feature.spot.usecase

import com.cmc.domain.feature.spot.model.Review
import com.cmc.domain.feature.spot.repository.SpotRepository
import javax.inject.Inject

class CreateReviewUseCase @Inject constructor(
    private val spotRepository: SpotRepository
) {
    suspend operator fun invoke(reviewId: Int, reviewText: String): Result<Review> {
        return spotRepository.createReview(reviewId, reviewText)
    }
}