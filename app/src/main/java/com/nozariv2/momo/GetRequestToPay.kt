package com.nozariv2.momo


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetRequestToPay(
    @Json(name = "amount")
    val amount: Int,
    @Json(name = "currency")
    val currency: String,
    @Json(name = "externalId")
    val externalId: Int,
    @Transient
    @Json(name = "financialTransactionId")
    val financialTransactionId: Int? = 0,
    @Json(name = "payer")
    val payer: Payer,
    @Json(name = "status")
    val status: String
)