package com.nozariv2.cloudtranslate

import retrofit2.Response
import retrofit2.http.*

interface TranslationAPI {

    @POST("v2")
    suspend fun makeTranslationRequest(@Query("key") apiKey : String,
                         @Body translationRequest: TranslationRequest)
            : Response<TranslationResponse>

}