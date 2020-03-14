package com.nozariv2.books

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nozariv2.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NewBook : AppCompatActivity() {

    private lateinit var editWordView: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)

        editWordView = findViewById(R.id.book_name_text_input)
        val button = findViewById<Button>(R.id.create_book_button)

        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editWordView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                val bookName = editWordView.text.toString()
                val currentDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
                val currentFirebaseUser = FirebaseAuth.getInstance().currentUser
                currentFirebaseUser?.let {
                    var uid = currentFirebaseUser.uid
                    replyIntent.putExtra(USERID_REPLY,uid)
                }
                replyIntent.putExtra(BOOKNAME_REPLY, bookName)
                replyIntent.putExtra("date",currentDate)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val BOOKNAME_REPLY = "com.example.android.bookname.REPLY"
        const val USERID_REPLY = "com.example.android.userid.REPLY"
    }
}
