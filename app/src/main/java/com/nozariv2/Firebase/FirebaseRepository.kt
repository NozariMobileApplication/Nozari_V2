package com.nozariv2.Firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirebaseRepository {

    private var mutableLiveData = MutableLiveData<User>()
    val failedUser = User("Failed", "Failed", "Failed", "Failed",0)
    val db = Firebase.firestore

    fun getUserData(uuid : String) : MutableLiveData<User>{
        val docRef = db.collection("users").document(uuid)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject<User>()
            if(user==null){
                Log.i("null", "null USER")
            }
            Log.i("Succ", "Success USER Loading")
            Log.i("Succ", user?.fullName!!)
            mutableLiveData.value = user
        }

        docRef.get().addOnFailureListener{
            //TODO
            Log.i("ERR", it.message + " " + it.toString())
            Log.i("ERR", "Failed USER Loading")
            mutableLiveData.value = failedUser
        }
//        Log.i("val", mutableLiveData.value!!.fullName!!)
        return mutableLiveData
    }

    fun useUserToken(uuid : String, currentTokens : Int){
        val docRef = db.collection("users").document(uuid)
        docRef
            .update("tokens", currentTokens - 1)
            .addOnSuccessListener {
                Log.d("FBase", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w("FBase", "Error updating document", e) }
    }

}