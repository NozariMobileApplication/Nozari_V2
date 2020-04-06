package com.nozariv2.database.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nozariv2.Pages
import com.nozariv2.R
import com.nozariv2.books.Books
import com.nozariv2.books.SelectBook
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
        val bookIcon = inflater.inflate(R.layout.recyclerview_book_item,parent,false)
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

        holder.itemView.setOnTouchListener { v, event ->
            when (event.action){
                MotionEvent.ACTION_DOWN -> {
                    holder.bookItemView.startAnimation(AnimationUtils.loadAnimation(holder.bookItemView.context,R.anim.scale))
                    holder.bookItemView.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                    holder.bookItemView.invalidate()
                }
                MotionEvent.ACTION_UP ->{
                    holder.bookItemView.background.clearColorFilter()
                    holder.bookItemView.invalidate()
                }
            }
            false
        }

        holder.itemView.setOnClickListener(){
            val page=Page(0,current.bookId, LocalDateTime.now().format( DateTimeFormatter.ofPattern("dd-MM-yyyy")),imageUri,0)
            val pagesDoa = PageRoomDatabase.getDatabase(holder.itemView.context).pageDoa()
            pagesDoa.insertPage(page)
            (context as Activity).finish()
            Utils.startActivity(holder.itemView.context, Pages::class.java,current.bookId)
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
        fun startActivity(context: Context, clazz: Class<*>, bookId:Int) {

            val intent = Intent(context, clazz)
            intent.putExtra("BOOK_ID",bookId.toString())
            context.startActivity(intent)

        }
    }
}