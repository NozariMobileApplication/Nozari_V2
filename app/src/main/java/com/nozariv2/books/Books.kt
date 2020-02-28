package com.nozariv2.books

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nozariv2.R
import com.nozariv2.database.adapters.BookListAdapter
import com.nozariv2.database.tables.Book
import com.nozariv2.database.viewModels.BookViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Books : AppCompatActivity() {

    private lateinit var bookViewModel: BookViewModel
    private val newBookActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)

        val fab = findViewById<FloatingActionButton>(R.id.book_newbook_fab)
        fab.setOnClickListener {
            val intent = Intent(this, NewBook::class.java)
            startActivityForResult(intent, newBookActivityRequestCode)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.book_recyclerview)
        val adapter = BookListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager= GridLayoutManager(this,3)

        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)
        bookViewModel.allbooks.observe(this, Observer { books ->
            // Update the cached copy of the words in the adapter.
            books?.let { adapter.setBooks(it) }
        })


    }



//    Creating a new book
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newBookActivityRequestCode && resultCode == Activity.RESULT_OK) {

            var userId: String=""
            var createDate = LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
            var bookName: String=""

            data?.getStringExtra(NewBook.BOOKNAME_REPLY)?.let {
                bookName = it
            }
            data?.getStringExtra(NewBook.USERID_REPLY)?.let {
                userId = it
            }

            if (!userId.equals("")&&!bookName.equals(""))
            {
                var book = Book(0, userId, (createDate).toString(), bookName)
                bookViewModel.insert(book)
                Toast.makeText(applicationContext,R.string.book_created_string, Toast.LENGTH_LONG).show()
            }

        }
        else
        {
            Toast.makeText(applicationContext,R.string.new_book_error_string, Toast.LENGTH_LONG).show()
        }
    }
}
