package com.nozariv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity

class Google : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)
        val login = findViewById(R.id.google_sign_in) as Button

        login.setOnClickListener {
            //takePicture();

            startActivity(Intent(this@Google, MainActivityOld::class.java))
        }
    }
}
