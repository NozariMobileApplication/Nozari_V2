package com.nozariv2.cloudtranslate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Response

class TranslationViewModel : ViewModel() {

    val repository: TranslationRepository = TranslationRepository()

    fun makeTranslationRequest(apiKey : String, translationRequest: TranslationRequest
    ) : MutableLiveData<Response<TranslationResponse>> {

        return repository.getMutableLiveData(apiKey, translationRequest)

    }

}