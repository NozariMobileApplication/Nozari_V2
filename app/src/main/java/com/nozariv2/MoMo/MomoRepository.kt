package com.example.gridtester

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response


class MomoRepository
{

    var client: MomoApi

    val response : MutableLiveData<Response<tokenResponse>> by lazy {
        MutableLiveData<Response<tokenResponse>>()
    }

    private var mutableLiveData = MutableLiveData<Response<tokenResponse>>()
    private var mutableLiveDataRequest = MutableLiveData<Response<Void>>()
    private var requestToPayMLD = MutableLiveData<Response<Void>>()
    private var getRequestToPayMLD = MutableLiveData<Response<GetRequestToPay>>()
    private var aquireTokenMLD = MutableLiveData<Response<tokenResponse>>()
    val completableJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob)

    var tokenAquired = false
    private var authToken = ""

    init {

        client = RetrofitService.createService(MomoApi::class.java)

    }

    fun getMutableLiveData():MutableLiveData<Response<tokenResponse>> {
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {

                    val request = client.getToken("Basic ZTY4MzdhYWEtMjNmYi00OTA3LWE2ZDYtODE1OTk5NGY2MDc0OmQyMTVlNmQ5OWJmODQzNzk4YmY4OTViMTBhZWJjYmI2", "0427b14ab6244eaba83381f55b69d3de")

                    when{
                        request.isSuccessful -> {
                            Log.i("request is successful!", request.body()?.accessToken)
                            mutableLiveData.value = request
                        }
                        else -> {
                            Log.i("request wasn't success", request.code().toString())
                            Log.i("request wasn't success", request.message())
                            mutableLiveData.value = request

                        }
                    }

                } catch (e: HttpException) {
                    Log.i("HttpException!", e.message())

                } catch (e: Throwable) {
                    Log.i("Throwable!", e.message)
                }
            }
        }
        return mutableLiveData
    }

    /*fun makeRequestToPayPlaceholder(): MutableLiveData<Response<Void>> {
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {

                    val request = client.postRequestToPay(authorization, targetEnvironment, referenceId, subscriptionKey, requestToPay)
                    when{
                        request.isSuccessful -> {
                            Log.i("request is successful!", request.code().toString())
                            mutableLiveDataRequest.value = request
                        }
                        else -> {
                            Log.i("request wasn't success", request.code().toString())
                            Log.i("request wasn't success", request.message())
                            mutableLiveDataRequest.value = request
                        }
                    }

                } catch (e: HttpException) {
                    Log.i("HttpException!", e.message())

                } catch (e: Throwable) {
                    Log.i("Throwable!", e.message)
                }
            }
        }
        return mutableLiveDataRequest
    }
*/
    fun aquireOAuthToken() {
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {

                    val request = client.getToken("Basic ZTY4MzdhYWEtMjNmYi00OTA3LWE2ZDYtODE1OTk5NGY2MDc0OmQyMTVlNmQ5OWJmODQzNzk4YmY4OTViMTBhZWJjYmI2", "0427b14ab6244eaba83381f55b69d3de")

                    when{
                        request.isSuccessful -> {
                            Log.i("request is successful!", request.body()?.accessToken)
                            aquireTokenMLD.value = request
                        }
                        else -> {
                            Log.i("request wasn't success", request.code().toString())
                            Log.i("request wasn't success", request.message())
                            aquireTokenMLD.value = request

                        }
                    }

                } catch (e: HttpException) {
                    Log.i("HttpException!", e.message())

                } catch (e: Throwable) {
                    Log.i("Throwable!", e.message)
                }
            }
        }
    }

/*    fun make2calls() : Response<tokenResponse> {

        coroutineScope.launch {
             withContext(Dispatchers.Main) {
                try {

                    val request = client.getToken("Basic ZTY4MzdhYWEtMjNmYi00OTA3LWE2ZDYtODE1OTk5NGY2MDc0OmQyMTVlNmQ5OWJmODQzNzk4YmY4OTViMTBhZWJjYmI2", "0427b14ab6244eaba83381f55b69d3de")

                    when{
                        request.isSuccessful -> {
                            Log.i("request is successful!", request.body()?.accessToken)
                            return@withContext request
                        }
                        else -> {
                            Log.i("request wasn't success", request.code().toString())
                            Log.i("request wasn't success", request.message())
                            return@withContext request
                        }
                    }

                } catch (e: HttpException) {
                    Log.i("HttpException!", e.message())

                } catch (e: Throwable) {
                    Log.i("Throwable!", e.message)
                }
            }
        }




    }*/

    fun makeRequestToPay(authorization : String,
                         targetEnvironment : String,
                         referenceId : String,
                         requestToPay: RequestToPay){
        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {

                    val request = client.postRequestToPay(referenceId, targetEnvironment, "0427b14ab6244eaba83381f55b69d3de", authorization, requestToPay)
                    when{
                        request.isSuccessful -> {
                            Log.i("P: request successful", request.code().toString())
                            requestToPayMLD.value = request
                        }
                        else -> {
                            Log.i("P: request no success", request.code().toString())
                            Log.i("P: request no success", request.message())
                            requestToPayMLD.value = request
                        }
                    }

                } catch (e: HttpException) {
                    Log.i("HttpException!", e.message())

                } catch (e: Throwable) {
                    Log.i("Throwable!", e.message)
                }
            }
        }
    }

    fun makePayRequest(targetEnvironment : String,
                       referenceId : String,
                       requestToPay: RequestToPay) : MutableLiveData<Response<Void>>{

        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {

                    val request = client.getToken("Basic ZTY4MzdhYWEtMjNmYi00OTA3LWE2ZDYtODE1OTk5NGY2MDc0OjBhZDAzMWFmNzUzYzRlZDY4ZWFiYjBlYmE3MDU0ZTli", "0427b14ab6244eaba83381f55b69d3de")

                    when{
                        request.isSuccessful -> {
                            Log.i("T: request successful!", request.code().toString())

                            tokenAquired = true
                            authToken = request.body()!!.accessToken

                            makeRequestToPay("Bearer " + request.body()!!.accessToken, targetEnvironment, referenceId, requestToPay)
                            mutableLiveData.value = request
                        }
                        else -> {
                            tokenAquired = true
                            authToken = request.body()!!.accessToken
                            Log.i("T: request no success", request.code().toString())
                            Log.i("T: request no success", request.message())
                            mutableLiveData.value = request
                        }
                    }

                } catch (e: HttpException) {
                    Log.i("HttpException!", e.message())

                } catch (e: Throwable) {
                    Log.i("Throwable!", e.message)
                }
            }
        }
        return requestToPayMLD
    }

    fun getRequestToPayStatus(targetEnvironment : String,
                              referenceId : String) : MutableLiveData<Response<GetRequestToPay>>{

        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {

                    val request = client.getRequestToPay(referenceId, targetEnvironment, "0427b14ab6244eaba83381f55b69d3de", "Bearer " + "eyJ0eXAiOiJKV1QiLCJhbGciOiJSMjU2In0.eyJjbGllbnRJZCI6ImU2ODM3YWFhLTIzZmItNDkwNy1hNmQ2LTgxNTk5OTRmNjA3NCIsImV4cGlyZXMiOiIyMDIwLTAxLTMwVDAwOjQzOjQ5Ljc4MCIsInNlc3Npb25JZCI6IjY2ODRlOTlhLWRhNTctNGJiYi1hYTY5LTgwZTI0MWY2NjA1ZiJ9.QXmeJpM2gVql7NRgipkiam2T3mmZdnAqB3GmfLJQ3CFuawZPAT-qwwBPBkhUtSNzWjXXBPEoi6Ze1n0Rs_YDCiuT15aQdiCRYTRNDvCclgos9dOa7G35mGMkmPVqLHzXXaf7mmbX_K248M1qQS071guSYvUgEPUX8lpjpsFwuaWM6rp9cHPmJh_LGWVKlo_Tuua23qsbBUCQ2sIkuVkJHOps2aBthDOYsX5aUw7niIQ3AmuE3E7qNjcpXUm7bcjy55u5bGdlXZ1TXOLTYnVvPEXpi3kL_mFr0OlufB_t3sh-tgM1dLd8UxP98ymwbAZhVI1LBDS0ZuDcksCuZe7AfA")
                    when{
                        request.isSuccessful -> {
                            Log.i("G: request successful!", request.code().toString())

                            getRequestToPayMLD.value = request
                        }
                        else -> {
                            Log.i("G: request no success", request.code().toString())
                            Log.i("G: request no success", request.message())
                            getRequestToPayMLD.value = request
                        }
                    }

                } catch (e: HttpException) {
                    Log.i("HttpException!", e.message())

                } catch (e: JsonDataException) {

                    Log.i("JSONDATAEXCEPTION!", "MSG: " + e.message!!)

                } catch (e: Throwable) {

                    Log.i("Throwable!", "Throw!" + e)
                }
            }
        }
        return getRequestToPayMLD




    }



/*    fun getAuthorizationToken() : Response<tokenResponse>{



        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                try {

                    val request = client.getToken("Basic ZTY4MzdhYWEtMjNmYi00OTA3LWE2ZDYtODE1OTk5NGY2MDc0OmQyMTVlNmQ5OWJmODQzNzk4YmY4OTViMTBhZWJjYmI2", "0427b14ab6244eaba83381f55b69d3de")

                    when{
                        request.isSuccessful -> {
                            Log.i("request is successful!", request.body()?.accessToken)
                        }
                        else -> {
                            Log.i("request wasn't success", request.code().toString())
                            Log.i("request wasn't success", request.message())

                        }
                    }

                } catch (e: HttpException) {
                    Log.i("HttpException!", e.message())
                    throw e

                } catch (e: Throwable) {
                    Log.i("Throwable!", e.message)
                    throw e
                }
            }
        }

        return response

    }*/


}