package com.nozariv2.Firebase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class FirebaseRepository {

    private var mutableLiveData = MutableLiveData<User>()
    private var mutableLiveDataTokenResponse = MutableLiveData<Boolean>()
    private var mutableLiveDataWriteResponse = MutableLiveData<Boolean>()
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

    fun writeNewUserData(uuid: String, fullName: String, email: String, phoneNumber: String, languageSelection: String) : MutableLiveData<Boolean>{
        val user = hashMapOf(
            "email" to email,
            "fullName" to fullName,
            "languageSelection" to languageSelection,
            "phoneNumber" to phoneNumber,
            "tokens" to 0)

        db.collection("users").document(uuid)
            .set(user)
            .addOnSuccessListener { Log.d("SUCC", "DocumentSnapshot successfully written!")
                mutableLiveDataWriteResponse.value = true
            }
            .addOnFailureListener { e -> Log.w("ERR", "Error writing document", e)
                mutableLiveDataWriteResponse.value = false
            }
        return mutableLiveDataWriteResponse
    }



    fun useUserToken(uuid : String) :  MutableLiveData<Boolean>{
        val sfDocRef = db.collection("users").document(uuid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(sfDocRef)
            val newTokens = snapshot.getDouble("tokens")!! - 1
            transaction.update(sfDocRef, "tokens", newTokens)
        }.addOnSuccessListener { Log.d("SUCC", "Transaction success!")
            mutableLiveDataTokenResponse.value = true}
         .addOnFailureListener { e -> Log.w("ERR", "Transaction failure.", e)
             mutableLiveDataTokenResponse.value = false}

        return mutableLiveDataTokenResponse
    }

}