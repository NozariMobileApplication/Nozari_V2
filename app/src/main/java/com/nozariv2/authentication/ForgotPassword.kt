package com.nozariv2.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.nozariv2.Home
import com.nozariv2.R

class ForgotPassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val sendPasswordReset : Button = findViewById(R.id.buttonSendResetPasswordEmail)
        val editTextForgotPasswordEmail : EditText = findViewById(R.id.editTextForgotPasswordEmail)
        val progressBar : ProgressBar = findViewById(R.id.progressBarForgotPassword)

        val fAuth = FirebaseAuth.getInstance()

        sendPasswordReset.setOnClickListener {

            val email: String = editTextForgotPasswordEmail.text.toString().trim()

            if(TextUtils.isEmpty(email)){
                editTextForgotPasswordEmail.error = "Email is Required"
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            fAuth.sendPasswordResetEmail(email).addOnCompleteListener(this){ task ->

                if(task.isSuccessful){

                    progressBar.visibility = View.GONE
                    Toast.makeText(baseContext, "Password reset email has been sent",
                        Toast.LENGTH_LONG).show()

                    startActivity(Intent(this@ForgotPassword, Home::class.java))

                } else {

                    progressBar.visibility = View.GONE
                    Toast.makeText(baseContext, "Password Reset Failed : " + task.exception?.message,
                        Toast.LENGTH_LONG).show()

                }
            }
        }
    }
}
