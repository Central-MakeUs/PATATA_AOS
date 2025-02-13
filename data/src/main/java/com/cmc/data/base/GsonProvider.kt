package com.cmc.data.base

import com.cmc.data.feature.spot.model.SpotWithStatusResponseDtoDeserializer
import com.cmc.data.feature.spot.model.SpotWithStatusResponseDto
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonProvider {
    val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .registerTypeAdapter(SpotWithStatusResponseDto::class.java, SpotWithStatusResponseDtoDeserializer())
            .create()
    }
}