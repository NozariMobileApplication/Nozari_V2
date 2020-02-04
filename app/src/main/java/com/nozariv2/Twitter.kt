package com.nozariv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity

class Twitter : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)
        val login = findViewById(R.id.twitter_login_btn) as Button

        login.setOnClickListener {
            //takePicture();

            startActivity(Intent(this@Twitter, MainActivityOld::class.java))
        }
    }
}
