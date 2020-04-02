package com.nozariv2.cloudtranslate

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nozariv2.momo.GetRequestToPay
import com.nozariv2.momo.MomoApi
import com.nozariv2.momo.RetrofitService
import com.nozariv2.momo.tokenResponse
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response

class TranslationRepository {

    var client: TranslationAPI

    private var mutableLiveData = MutableLiveData<Response<TranslationResponse>>()
    val completableJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

    init {

        client = TranslationRetrofitService.createService(TranslationAPI::class.java)

    }

    fun getMutableLiveData(apiKey : String, translationRequest: TranslationRequest): MutableLiveData<Response<TranslationResponse>> {
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {

                    val request = client.makeTranslationRequest(apiKey, translationRequest)

                    when{
                        request.isSuccessful -> {
                            //Log.i("request is successful!", request.body()?.data?.translations?.get(0)?.translatedText)
                            mutableLiveData.value = request
                        }
                        else -> {
                            //Log.i("request wasn't success", request.code().toString())
                            //Log.i("request wasn't success", request.message())
                            mutableLiveData.value = request

                        }
                    }

                } catch (e: HttpException) {
                    Log.i("HttpException!", e.message())

                } catch (e: Throwable) {
                    Log.i("ERR", "Translation Throwable!")
                    Log.i("Throwable!", e.message)
                }
            }
        }
        return mutableLiveData
    }

}