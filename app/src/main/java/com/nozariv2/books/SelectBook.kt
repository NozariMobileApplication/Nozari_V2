package com.nozariv2.books

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nozariv2.Pages
import com.nozariv2.R
import com.nozariv2.database.adapters.SelectBookListAdapter
import com.nozariv2.database.roomdatabase.PageRoomDatabase
import com.nozariv2.database.tables.Book
import com.nozariv2.database.tables.Page
import com.nozariv2.database.viewModels.BookViewModel
import com.nozariv2.databinding.ActivitySelectbookBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SelectBook : AppCompatActivity() {

    private lateinit var binding: ActivitySelectbookBinding
    private lateinit var imageURI: String

    private lateinit var bookViewModel: BookViewModel
    private val newBookActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_selectbook)

        imageURI = intent.getStringExtra("IMAGE_PATH")

        val recyclerView = findViewById<RecyclerView>(R.id.selectbook_recyclerview)
        val adapter = SelectBookListAdapter(this, imageURI)
        recyclerView.adapter = adapter
        recyclerView.layoutManager= GridLayoutManager(this,3)

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel::class.java)
        bookViewModel.allbooks.observe(this, Observer { books ->
            // Update the cached copy of the words in the adapter.
            books?.let { adapter.setBooks(it) }
        })

        val fab = findViewById<FloatingActionButton>(R.id.newbook_fab)
        fab.setOnClickListener {
            val intent = Intent(this, NewBook::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivityForResult(intent, newBookActivityRequestCode)
        }

        binding.selectbooksSearchbar.addTextChangedListener { text -> bookViewModel.searchNameChanged(text.toString()) }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newBookActivityRequestCode && resultCode == Activity.RESULT_OK) {

            var id: String
            var userId: String="test"
            //val date = "Test Date"
//            var date = LocalDate.parse(LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString(), DateTimeFormatter.ofPattern("dd-MM-yyyy"))
//            var date= SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()).toString()
            var createDate = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()).toString()
            var bookName: String=""
            var uri:String=""

            data?.getStringExtra(NewBook.BOOKNAME_REPLY)?.let {
                bookName = it
            }
            data?.getStringExtra(NewBook.USERID_REPLY)?.let {
                userId = it
            }

            data?.getStringExtra(NewBook.URI_REPLY)?.let {
                uri = it
            }

            if (!userId.equals("")&&!bookName.equals(""))
            {
                var book = Book(0, userId, (createDate), (createDate), bookName,uri)
                bookViewModel.insert(book)
                Toast.makeText(applicationContext,R.string.book_created_string,Toast.LENGTH_LONG).show()
//                Toast.makeText(applicationContext,bookViewModel.getId(bookName).toString(),Toast.LENGTH_LONG).show()

//                val page= Page(0,bookViewModel.getId(bookName), LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy")),imageURI,0)
                val page= Page(0,bookViewModel.getId(bookName), SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()).toString(),imageURI,0)
                val pagesDoa = PageRoomDatabase.getDatabase(this).pageDoa()
                pagesDoa.insertPage(page)
                (this as Activity).finish()
                Utils.startActivity(this, Pages::class.java,bookViewModel.getId(bookName) )

                /*startActivity(Intent(this, Books::class.java))
                finish()*/
            }

        }
        else{Toast.makeText(applicationContext,R.string.new_book_error_string,Toast.LENGTH_LONG).show()}

        //finish()

    }
}

class Utils {

    companion object {
        fun startActivity(context: Context, clazz: Class<*>, bookId:Int) {

            val intent = Intent(context, clazz)
            intent.putExtra("BOOK_ID",bookId.toString())
            context.startActivity(intent)

        }
    }
}

