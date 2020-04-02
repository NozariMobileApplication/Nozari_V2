package com.nozariv2.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.nozariv2.Firebase.User
import com.nozariv2.Firebase.UsersViewModel
import com.nozariv2.Home
import com.nozariv2.R
import java.util.*

class Login : AppCompatActivity() {

    lateinit var fAuth : FirebaseAuth
    lateinit var user : User
    var mainViewModel: UsersViewModel? = null

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

        fAuth = FirebaseAuth.getInstance()
        mainViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)

        fAuth.signOut()

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
                        //progressBar.visibility = View.GONE
                        Log.d("Success", "signInWithEmail:success")

                        pullUserData(mainViewModel!!)

/*                        if(pullUserData(mainViewModel!!).fullName.equals("Failed")){
                            Log.i("LOG", "Signing out user, failed to load data" )
                            fAuth.signOut()
                        } else {
                            Log.i("LOG", "Starting activity, load success" )
                            startActivity(Intent(this@Login, Home::class.java))
                                //add the user as an extra, do the same for register9
                        }*/


                        //startActivity(Intent(this@Login, Home::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        progressBar.visibility = View.INVISIBLE
                        it.isClickable = true
                        Log.w("Error", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Login Failed : " + task.exception?.message,
                            Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener(this){
                    Toast.makeText(baseContext, "Login Failed : " + it.message,
                        Toast.LENGTH_LONG).show()
                }
        }
    }

    fun pullUserData(viewModel : UsersViewModel){
        val progressBar: ProgressBar = findViewById(R.id.progressBarLogin)
        Log.i("LOG", "in Pull User Data" )
        viewModel.getUser(fAuth.currentUser!!.uid).observe(this, Observer {
            if(it.fullName.equals("Failed")){
                Log.i("LOG", "in failed" )
                Toast.makeText(this,"Failed to load your data. Please ensure you have an internet connection and try again.", Toast.LENGTH_LONG).show()
                user = it
                Log.i("LOG", "Signing out user, failed to load data" )
                fAuth.signOut()
                progressBar.visibility = View.GONE
            } else {
                Log.i("LOG", "in success" )
                user = it
                Log.i("LOG", "Starting activity, load success" )
                startActivity(Intent(this@Login, Home::class.java).apply {
                    this.putExtra("user", user)
                })
                finish()
                progressBar.visibility = View.GONE
                //change UI elements
            }
        })
    }

    override fun onBackPressed() {
        //doNothing here. Want to force user to go through login process before getting to Home again.
    }



}
