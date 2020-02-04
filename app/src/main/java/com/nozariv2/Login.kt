package com.nozariv2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //val relative = findViewById<View>(R.id.imageView5) as ImageView
        //relative.setBackgroundResource(0)

        val login = findViewById<View>(R.id.login) as Button
        val Create = findViewById<View>(R.id.creating_account) as Button

        val twitter = findViewById<View>(R.id.twitter) as Button

        Create.setOnClickListener {
            startActivity(Intent(this@Login, New_Account::class.java))
        }

        login.setOnClickListener {
            startActivity(Intent(this@Login, Home::class.java))
        }
    }


}
