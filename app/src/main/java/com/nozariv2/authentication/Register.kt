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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.nozariv2.Firebase.User
import com.nozariv2.Firebase.UsersViewModel
import com.nozariv2.Home
import com.nozariv2.R

class Register : AppCompatActivity() {

    var mainViewModel: UsersViewModel? = null

    lateinit var user : User
    lateinit var fAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mainViewModel = ViewModelProvider(this).get(UsersViewModel::class.java)

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

        fAuth = FirebaseAuth.getInstance()
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

                        mainViewModel!!.writeNewUserData(user!!.uid, fullName, email, phoneNumber, languageSelection).observe(this, Observer { isSuccessful ->
                            if(isSuccessful){
                                Toast.makeText(baseContext, "Registration Successful!", Toast.LENGTH_SHORT).show()
                                pullUserData(mainViewModel!!)
                            } else {
                                //The user was created in Firebase Auth, but the user details were not recorded.
                                //We need to delete their account and display a message to the user.
                                user.delete().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(baseContext, "Registration failed. Please try again later or contact support.", Toast.LENGTH_LONG).show()
                                        progressBarRegister.visibility = View.GONE
                                        it.isClickable = true
                                    }
                                }
                            }
                        })
                    } else {
                        // If sign in fails, display a message to the user.
                        progressBarRegister.visibility = View.GONE
                        it.isClickable = true
                        Log.w("Error", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Registration Failed : " + task.exception?.message,
                            Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    fun pullUserData(viewModel : UsersViewModel){
        val progressBar: ProgressBar = findViewById(R.id.progressBarRegister)
        Log.i("LOG", "in Pull User Data" )
        viewModel.getUser(fAuth.currentUser!!.uid).observe(this, Observer {
            if(it.fullName.equals("Failed")){
                Log.i("LOG", "in failed" )
                Toast.makeText(this,"Failed to load your data. Please ensure you have an internet connection and try again.", Toast.LENGTH_LONG).show()
                user = it
                Log.i("LOG", "Signing out user, failed to load data" )
                fAuth.signOut()
                startActivity(Intent(this@Register, Login::class.java))
                finish()
                progressBar.visibility = View.GONE
            } else {
                Log.i("LOG", "in success" )
                user = it
                Log.i("LOG", "Starting activity, load success" )
                startActivity(Intent(this@Register, Home::class.java).apply {
                    this.putExtra("user", user)
                })
                finish()
                progressBar.visibility = View.GONE
                //change UI elements
            }
        })
    }


}
