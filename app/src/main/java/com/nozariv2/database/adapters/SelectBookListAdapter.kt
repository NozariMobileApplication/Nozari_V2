package com.nozariv2.database.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.nozariv2.R
import com.nozariv2.books.Books
import com.nozariv2.database.roomdatabase.PageRoomDatabase
import com.nozariv2.database.tables.Book
import com.nozariv2.database.tables.Page
import org.jetbrains.anko.doAsync
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SelectBookListAdapter constructor(context: Context, imageUri:String) : RecyclerView.Adapter<SelectBookListAdapter.BookViewHolder>(){

    val imageUri=imageUri
    val context = context

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var books = emptyList<Book>() // Cached copy of books

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookItemView: TextView = itemView.findViewById(R.id.recyclerBookView)
        val bookIcon: ImageView = itemView.findViewById(R.id.book_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_book_item, parent, false)
        return BookViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val current = books[position]
        holder.bookItemView.text = current.bookName.toString()

        doAsync {
            val bitmap = BitmapFactory.decodeFile(current.uri)
            var resized = Bitmap.createScaledBitmap(bitmap, 150, 200, false)
            holder.bookIcon.setImageBitmap(resized)
        }

        holder.itemView.setOnClickListener(){
            val page=Page(0,current.bookId, LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy")),imageUri,0)
            val pagesDoa = PageRoomDatabase.getDatabase(holder.itemView.context).pageDoa()
            pagesDoa.insertPage(page)
            Utils.startActivity(holder.itemView.context,Books::class.java )
        }
    }

    internal fun setBooks(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    override fun getItemCount() = books.size

}

class Utils {

    companion object {
        fun startActivity(context: Context, clazz: Class<*>) {

            val intent = Intent(context, clazz)
            context.startActivity(intent)

        }
    }
}