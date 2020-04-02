package com.nozariv2

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.nozariv2.books.SelectBook
import java.io.File

class OverlayedPageDisplay : AppCompatActivity() {

    var backPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_overlayed_page_display)

        findViewById<ImageView>(R.id.overlayedPageDisplayImageView).apply {

            val imageFile = File(intent.getStringExtra("fpath"))
            if(imageFile.exists()){
                this.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra("fpath")))
            }
        }

        val confirm = findViewById<View>(R.id.confirm_page_button) as Button
        confirm.setOnClickListener{
            savePage()
        }
    }

    override fun onBackPressed() {
        if(!backPressed){
            Toast.makeText(this,"Press back again to lose translation, or save it to a book.", Toast.LENGTH_LONG).show()
            backPressed = true
        } else {
            val intent = Intent(this, Home::class.java)
            finish()
            startActivity(intent)
        }
    }

    fun savePage(){
        val intent = Intent(this, SelectBook::class.java).apply {
            putExtra("IMAGE_PATH", intent.getStringExtra("fpath"))
            this.setData(intent.data)
            //this.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        }
        startActivity(intent)
    }


}
