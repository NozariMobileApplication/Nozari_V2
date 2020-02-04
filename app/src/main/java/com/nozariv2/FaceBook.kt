package com.nozariv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button

import androidx.appcompat.app.AppCompatActivity

class FaceBook : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_book)
        val login = findViewById(R.id.face_book_login_btn) as Button

        login.setOnClickListener {
            //takePicture();

            startActivity(Intent(this@FaceBook, MainActivityOld::class.java))
        }
    }
}
