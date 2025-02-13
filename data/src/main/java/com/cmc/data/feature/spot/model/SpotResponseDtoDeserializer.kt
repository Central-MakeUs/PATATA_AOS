package com.cmc.data.feature.spot.model

import com.cmc.domain.base.exception.InvalidJsonFormatException
import com.google.gson.*
import java.lang.reflect.Type
class SpotWithStatusResponseDtoDeserializer : JsonDeserializer<SpotWithStatusResponseDto> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): SpotWithStatusResponseDto {
        return try {
            val jsonObject = json.asJsonObject

            if (!jsonObject.has("spotId") || !jsonObject.has("spotName")) {
                throw InvalidJsonFormatException("필수 필드 누락: $jsonObject")
            }

            val spot = SpotResponseDto(
                spotId = jsonObject.get("spotId").asInt,
                address = jsonObject.get("spotAddress")?.asString ?: "주소 없음",
                spotName = jsonObject.get("spotName")?.asString ?: "이름 없음",
                categoryId = jsonObject.get("categoryId")?.asInt ?: -1,
                tags = jsonObject.getAsJsonArray("tags")?.map { it.asString } ?: emptyList(),
            )

            SpotWithStatusResponseDto(
                spot = spot,
                imageUrl = jsonObject.get("imageUrl")?.takeIf { !it.isJsonNull }?.asString ?: "",
                reviewCount = jsonObject.get("reviews")?.takeIf { !it.isJsonNull }?.asInt ?: 0,
                scrapCount = jsonObject.get("spotScraps")?.takeIf { !it.isJsonNull }?.asInt ?: 0,
                isScraped = jsonObject.get("isScraped")?.takeIf { !it.isJsonNull }?.asBoolean ?: false
            )
        } catch (e: Exception) {
            e.stackTrace
            throw e
        }
    }
}
