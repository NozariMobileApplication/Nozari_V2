package com.nozariv2.cloudtranslate

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TranslationRequest(
    @Json(name = "q")
    val q: String,
    @Json(name = "target")
    val target: String,
    @Json(name = "source")
    val source: String,
    @Json(name = "format")
    val format: String
)