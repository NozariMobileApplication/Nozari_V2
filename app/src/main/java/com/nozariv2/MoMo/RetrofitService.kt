package com.example.gridtester

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

public object RetrofitService {

    var logging   = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    var client : OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

    private var retrofit : Retrofit = Retrofit.Builder()
        .baseUrl("https://sandbox.momodeveloper.mtn.com/collection/")
        .addConverterFactory(MoshiConverterFactory.create().asLenient())
        .client(client)
        .build()

    public fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }


}