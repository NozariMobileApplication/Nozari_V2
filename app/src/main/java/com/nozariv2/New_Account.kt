package com.nozariv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner

import androidx.appcompat.app.AppCompatActivity

class New_Account : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new__account)
        val login = findViewById(R.id.login_new_account) as Button


        val back = findViewById(R.id.Back_to_login) as Button

        back.setOnClickListener {
            finish()
        }

        val spinner: Spinner = findViewById(R.id.spinner)
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

        login.setOnClickListener {
            //takePicture();

            startActivity(Intent(this@New_Account, Home::class.java).apply {
                this.putExtra("language_selection", getLanguageCode(spinner.selectedItem.toString() ))

            })
        }

    }

    fun getLanguageCode(language: String): String {
        when(language){
            "Zulu" -> return "zu"
            "isiXhosa" -> return "xh"
            "Shona" -> return "sn"
            else -> {
                return "zu"
            }
        }
    }

}
