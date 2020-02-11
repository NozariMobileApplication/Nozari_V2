package com.nozariv2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.nozariv2.books.SelectBook

class OCRCameraNew : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocrcamera_new)

        val imageURI = intent.getStringExtra("IMAGE_URI")
        val imageView = findViewById<ImageView>(R.id.ocrCameraImageView).apply {
            this.setImageURI(Uri.parse(imageURI))
        }

        val textView =  findViewById<TextView>(R.id.ocrTextView).apply{
            this.text = intent.getStringExtra("translated_text")
        }

        val confirm = findViewById<View>(R.id.confirm_page_button) as ImageView
        confirm.setOnClickListener{
            savePage()
        }

    }

    fun savePage(){
        val intent = Intent(this, SelectBook::class.java).apply {
            putExtra("IMAGE_URI", intent.getStringExtra("IMAGE_URI"))
            this.setData(intent.data)
        }
        startActivity(intent)
    }
}
