package com.example.gridtester

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Response

class MomoViewModel : ViewModel() {

    val repository: MomoRepository = MomoRepository()

    val tokenResponse : MutableLiveData<Response<tokenResponse>> get() = repository.getMutableLiveData()

    //val requestToPayResponse : MutableLiveData<Response<Void>> get() = repository.makePayRequest()

    fun postRequestToPay(targetEnvironment : String,
                         referenceId : String,
                         requestToPay: RequestToPay) : MutableLiveData<Response<Void>> {

        return repository.makePayRequest(targetEnvironment, referenceId, requestToPay)

    }

    fun getRequestToPay(targetEnvironment : String,
                        referenceId : String) : MutableLiveData<Response<GetRequestToPay>> {

        return repository.getRequestToPayStatus(targetEnvironment, referenceId)

    }

    //val liveData = MutableLiveData<Response<tokenResponse>>()
/*


    val currentName: MutableLiveData<Response<tokenResponse>> by lazy {
        MutableLiveData<Response<tokenResponse>>()



    }

    val response : MutableLiveData<Response<tokenResponse>> by lazy {
        MutableLiveData<Response<tokenResponse>>()

    }*/
/*
    fun getToken (authorization : String, subscriptionKey : String) : Response<tokenResponse>{

        val response : MutableLiveData<Response<tokenResponse>> = (Dispatchers.IO)



*//*        val response = LiveData<Response<tokenResponse>>() = liveData(Dispatchers.IO){

            val retrievedData = repoistory.getToken(authorization, subscriptionKey)
            emit(retrievedData)

        }


        liveData(Dispatchers.IO) {

            emit(repoistory.getToken(authorization, subscriptionKey))

        }*//*

    }*/
}