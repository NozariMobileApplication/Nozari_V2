package com.example.gridtester


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestToPay(
    @Json(name = "amount")
    val amount: String,
    @Json(name = "currency")
    val currency: String,
    @Json(name = "externalId")
    val externalId: String,
    @Json(name = "payer")
    val payer: Payer,
    @Json(name = "payerMessage")
    val payerMessage: String,
    @Json(name = "payeeNote")
    val payeeNote: String
)