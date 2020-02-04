package com.example.gridtester


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Payer(
    @Json(name = "partyIdType")
    val partyIdType: String,
    @Json(name = "partyId")
    val partyId: String
)