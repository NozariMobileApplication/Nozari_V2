package com.nozariv2.cloudtranslate

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

public object TranslationRetrofitService {

    var logging   = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    var client : OkHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()

    private var retrofit : Retrofit = Retrofit.Builder()
        .baseUrl("https://translation.googleapis.com/language/translate/")
        .addConverterFactory(MoshiConverterFactory.create().asLenient())
        .client(client)
        .build()

    public fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }

}