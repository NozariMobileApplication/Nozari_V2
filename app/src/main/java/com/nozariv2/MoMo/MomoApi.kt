package com.example.gridtester

import retrofit2.Response
import retrofit2.http.*

interface MomoApi {

    @POST("token/")
    suspend fun getToken(@Header("Authorization") authorization : String,
                         @Header("Ocp-Apim-Subscription-Key") subscriptionKey : String)
            : Response<tokenResponse>

/*    @Headers(
        "Content-Type: application/json")*/
    @POST("v1_0/requesttopay")
    suspend fun postRequestToPay(@Header("X-Reference-Id") referenceId : String,
                                 @Header("X-Target-Environment") targetEnvironment : String,
                                 @Header("Ocp-Apim-Subscription-Key") subscriptionKey : String,
                                 @Header("Authorization") authorization : String,
                                 @Body requestToPay: RequestToPay)
            : Response<Void>

    @GET("v1_0/requesttopay/{referenceId}")
    suspend fun getRequestToPay(@Path("referenceId") referenceId: String,
                                @Header("X-Target-Environment") targetEnvironment : String,
                                @Header("Ocp-Apim-Subscription-Key") subscriptionKey : String,
                                @Header("Authorization") authorization: String)
            : Response<GetRequestToPay>



}