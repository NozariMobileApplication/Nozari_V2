package com.nozariv2.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nozariv2.Home
import com.nozariv2.R

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //val relative = findViewById<View>(R.id.imageView5) as ImageView
        //relative.setBackgroundResource(0)

        val login = findViewById<View>(R.id.login) as Button
        val create = findViewById<View>(R.id.creating_account) as Button
        val forgotPassword: TextView = findViewById(R.id.textViewForgotPassword)

        forgotPassword.setOnClickListener{
            startActivity(Intent(this@Login, ForgotPassword::class.java))
        }

        create.setOnClickListener {
            startActivity(Intent(this@Login, Register::class.java))
        }

        val textEmail: EditText = findViewById(R.id.textLoginEmail)
        val textPassword: EditText = findViewById(R.id.textLoginPassword)

        val progressBar: ProgressBar = findViewById(R.id.progressBarLogin)

        val fAuth = FirebaseAuth.getInstance()

        login.setOnClickListener {

            val email: String = textEmail.text.toString().trim()
            val password: String = textPassword.text.toString().trim()

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

            progressBar.visibility = View.VISIBLE
            it.isClickable = false

            //TODO THIS NEEDS A TRY CATCH FOR NO CONNECTION & ERROR CATCHING

            fAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        progressBar.visibility = View.GONE
                        Log.d("Success", "signInWithEmail:success")
                        val user = fAuth.currentUser

                        startActivity(Intent(this@Login, Home::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        progressBar.visibility = View.INVISIBLE
                        it.isClickable = true
                        Log.w("Error", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Login Failed : " + task.exception?.message,
                            Toast.LENGTH_LONG).show()
                    }

                    // ...
                }


        }


    }


}
