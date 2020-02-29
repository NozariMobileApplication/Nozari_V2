package com.nozariv2.cloudtranslate


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "translations")
    val translations: List<Translation>
)