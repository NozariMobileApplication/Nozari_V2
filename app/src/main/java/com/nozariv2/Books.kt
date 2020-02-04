package com.nozariv2

import android.os.Bundle
import android.widget.GridView
import android.widget.TableLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_books.*

class Books : AppCompatActivity() {

    val numbers = mutableListOf(
        "A", "B", "C", "D", "E",
        "F", "G", "H", "I", "J",
        "K", "L", "M", "N", "O",
        "P", "Q", "R", "S", "T",
        "U", "V", "W", "X", "Y", "Z", "\n","\n","\n","\n",
        "a", "b","c","d","e",
        "f","g","h","i","j",
        "k","l","m","n","o",
        "p","q","r","s","t",
        "u","v","w","x","y","z")


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val gridView = findViewById<GridView>(R.id.booksGridView)


    }

}
