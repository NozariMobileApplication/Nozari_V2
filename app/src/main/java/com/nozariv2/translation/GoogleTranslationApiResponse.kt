package com.example.gridtester


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleTranslationApiResponse(
    @Json(name = "data")
    val `data`: Data
)