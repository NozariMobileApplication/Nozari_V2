package com.nozariv2

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import java.io.File

class OverlayedPageDisplay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlayed_page_display)

        findViewById<ImageView>(R.id.overlayedPageDisplayImageView).apply {

            val imageFile = File(intent.getStringExtra("fpath"))
            if(imageFile.exists()){
                this.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra("fpath")))
            }
        }
    }
}
