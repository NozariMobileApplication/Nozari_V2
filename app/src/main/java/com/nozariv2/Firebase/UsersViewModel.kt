package com.nozariv2.Firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Response

class UsersViewModel : ViewModel() {

    val repository: FirebaseRepository = FirebaseRepository()

    fun getUser(uuid : String) : MutableLiveData<User> {
        Log.i("uuid", uuid)
        return repository.getUserData(uuid)
    }

    fun useUserToken(uuid : String) : MutableLiveData<Boolean>{
        return repository.useUserToken(uuid)
    }

    fun writeNewUserData(uuid: String, fullName: String, email: String, phoneNumber: String, languageSelection: String) : MutableLiveData<Boolean>{
        return repository.writeNewUserData(uuid, fullName, email, phoneNumber, languageSelection)
    }

    }