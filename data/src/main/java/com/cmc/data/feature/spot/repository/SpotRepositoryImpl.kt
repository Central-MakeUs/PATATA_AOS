package com.cmc.data.feature.spot.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.cmc.data.base.apiRequestCatching
import com.cmc.data.feature.spot.CategorySpotPagingSource
import com.cmc.data.feature.spot.model.CreateReviewRequest
import com.cmc.data.feature.spot.model.toDomain
import com.cmc.data.feature.spot.model.toListDomain
import com.cmc.data.feature.spot.remote.SpotApiService
import com.cmc.domain.feature.spot.base.PaginatedResponse
import com.cmc.domain.feature.spot.model.Review
import com.cmc.domain.feature.spot.model.SpotDetail
import com.cmc.domain.feature.spot.model.SpotWithStatus
import com.cmc.domain.feature.spot.repository.SpotRepository
import com.cmc.domain.model.ImageData
import com.cmc.domain.model.SpotCategory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class SpotRepositoryImpl @Inject constructor(
    private val spotApiService: SpotApiService,
): SpotRepository {

    override suspend fun getPaginatedCategorySpots(
        categoryId: Int,
        latitude: Double,
        longitude: Double,
        sortBy: String
    ): PaginatedResponse<SpotWithStatus> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CategorySpotPagingSource(spotApiService, categoryId, latitude, longitude, sortBy)
            }
        ).flow
    }

    override suspend fun getCategorySpots(
        categoryId: Int,
        latitude: Double,
        longitude: Double,
        sortBy: String
    ): Result<List<SpotWithStatus>> {
        return apiRequestCatching(
            apiCall = { spotApiService.getCategorySpots(
                categoryId = if (categoryId == SpotCategory.ALL.id) null else categoryId,
                page = 0,
                latitude = latitude,
                longitude = longitude,
                sortBy = sortBy
            )},
            transform = { it.toListDomain() }
        )
    }

    override suspend fun getSpotDetail(spotId: Int): Result<SpotDetail> {
        return apiRequestCatching(
            apiCall = { spotApiService.getSpotDetail(spotId) },
            transform = { it.toDomain() }
        )
    }

    override suspend fun createSpot(
        spotName: String,
        spotDesc: String?,
        spotAddress: String,
        spotAddressDetail: String?,
        latitude: Double,
        longitude: Double,
        categoryId: Int,
        tags: List<String>?,
        images: List<ImageData>
    ): Result<Unit> {
        return runCatching {
            val imageParts = createImageMultipart(images)
            val textParts = createSpotRequestToMultipart(
                spotName, spotDesc, spotAddress, spotAddressDetail, latitude, longitude, categoryId, tags
            )

            spotApiService.createSpot(
                spotName = textParts["spotName"]!!,
                spotDesc = textParts["spotDesc"],
                spotAddress = textParts["spotAddress"]!!,
                spotAddressDetail = textParts["spotAddressDetail"],
                latitude = textParts["latitude"]!!,
                longitude = textParts["longitude"]!!,
                categoryId = textParts["categoryId"]!!,
                tags = textParts["tags"],
                images = imageParts
            )
        }
    }

    override suspend fun deleteSpot(spotId: Int): Result<Unit> {
        return apiRequestCatching(
            apiCall = { spotApiService.deleteSpot(spotId) },
            transform = {}
        )
    }

    override suspend fun toggleSpotScrap(spotId: Int): Result<Unit> {
        return apiRequestCatching(
            apiCall = { spotApiService.toggleSpotScrap(spotId) },
            transform = {}
        )
    }

    override suspend fun createReview(spotId: Int, reviewText: String): Result<Review> {
        return apiRequestCatching(
            apiCall = { spotApiService.createReview(
                CreateReviewRequest(spotId, reviewText)
            )},
            transform = { it.toDomain() }
        )
    }

    override suspend fun deleteReview(reviewId: Int): Result<Unit> {
        return apiRequestCatching(
            apiCall = { spotApiService.deleteReview(reviewId) },
            transform = {}
        )
    }

    private fun createSpotRequestToMultipart(
        spotName: String,
        spotDesc: String?,
        spotAddress: String,
        spotAddressDetail: String?,
        latitude: Double,
        longitude: Double,
        categoryId: Int,
        tags: List<String>?
    ): Map<String, RequestBody> {
        return mapOf(
            "spotName" to spotName.toRequestBody(),
            "spotDesc" to (spotDesc ?: "").toRequestBody(),
            "spotAddress" to spotAddress.toRequestBody(),
            "spotAddressDetail" to (spotAddressDetail ?: "").toRequestBody(),
            "latitude" to latitude.toString().toRequestBody(),
            "longitude" to longitude.toString().toRequestBody(),
            "categoryId" to categoryId.toString().toRequestBody(),
            "tags" to (tags?.joinToString(",") ?: "").toRequestBody()
        )
    }

    private fun String.toRequestBody(): RequestBody =
        this.toRequestBody("text/plain".toMediaTypeOrNull())

    private fun createImageMultipart(imageDataList: List<ImageData>): List<MultipartBody.Part> {
        return imageDataList.flatMapIndexed { index, imageData ->
            val imageFilePart = MultipartBody.Part.createFormData(
                "images[$index].file",
                imageData.fileName,
                RequestBody.create(imageData.mimeType.toMediaTypeOrNull(), imageData.byteArray)
            )

            val isRepresentativePart = MultipartBody.Part.createFormData(
                "images[$index].isRepresentative",
                if (index == 0) true.toString() else false.toString()
            )

            val sequencePart = MultipartBody.Part.createFormData(
                "images[$index].sequence",
                index.toString()
            )

            listOf(imageFilePart, isRepresentativePart, sequencePart)
        }
    }

}