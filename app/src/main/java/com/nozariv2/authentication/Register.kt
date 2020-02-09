package com.nozariv2.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.nozariv2.Home
import com.nozariv2.R

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Populate spinner with languages

        val spinner: Spinner = findViewById(R.id.spinnerLanguage)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.languages_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        val textFullName: EditText = findViewById(R.id.textFullName)
        val textEmail: EditText = findViewById(R.id.textEmail)
        val textPhoneNumber: EditText = findViewById(R.id.textPhoneNumber)
        val textPassword: EditText = findViewById(R.id.textPassword)
        val buttonRegister: Button =  findViewById(R.id.buttonRegister)
        val progressBarRegister: ProgressBar = findViewById(R.id.progressBarRegister)

        val fAuth : FirebaseAuth = FirebaseAuth.getInstance()
        val fStore : FirebaseFirestore = FirebaseFirestore.getInstance()

        if(fAuth.currentUser != null){
            startActivity(Intent(this@Register, Home::class.java))
        }

        buttonRegister.setOnClickListener {

            val email: String = textEmail.text.toString().trim()
            val password: String = textPassword.text.toString().trim()
            val fullName: String = textFullName.text.toString()
            val phoneNumber: String = textPhoneNumber.text.toString()
            val languageSelection: String = spinner.selectedItem.toString()

            if(TextUtils.isEmpty(email)){
                textEmail.error = "Email is Required"
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(password)){
                textPassword.error = "Password is Required"
                return@setOnClickListener
            }

            if(password.length < 6){
                textPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            progressBarRegister.visibility = View.VISIBLE

            it.isClickable = false

            fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Success", "createUserWithEmail:success")
                        val user = fAuth.currentUser

                        val documentReference: DocumentReference = fStore.collection("users").document(user!!.uid)
                        val userData: HashMap<String, Any> = HashMap()
                        userData.put("FullName", fullName)
                        userData.put("Email", email)
                        userData.put("PhoneNumber", phoneNumber)
                        userData.put("LanguageSelection", languageSelection)
                        userData.put("Tokens", 0)
                        documentReference.set(userData)
                            .addOnSuccessListener(this) {

                                Log.i("Success", "User " + user.uid + " data written successfully")

                            }
                            .addOnFailureListener(this) {

                                Log.i("Error", "User data not written " +  task.exception!!.message)

                            }

                        Toast.makeText(baseContext, "Registration Successful!",
                            Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Register, Home::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        progressBarRegister.visibility = View.GONE
                        it.isClickable = true
                        Log.w("Error", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Registration Failed : " + task.exception?.message,
                            Toast.LENGTH_LONG).show()
                    }

                    // ...
                }

        }

    }


}
