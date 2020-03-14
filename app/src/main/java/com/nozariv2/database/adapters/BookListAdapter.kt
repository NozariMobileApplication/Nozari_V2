package com.nozariv2.database.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nozariv2.Pages
import com.nozariv2.R
import com.nozariv2.database.tables.Book

class BookListAdapter internal constructor(context: Context) : RecyclerView.Adapter<BookListAdapter.BookViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var books = emptyList<Book>() // Cached copy of books

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookItemView: TextView = itemView.findViewById(R.id.recyclerBookView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_book_item, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val current = books[position]
        holder.bookItemView.text = current.bookId.toString()

        holder.itemView.setOnClickListener(){
            holder.bookItemView.text=current.bookName
            Utils.startActivity(holder.itemView.context, Pages::class.java,current.bookId )
        }
    }

    internal fun setBooks(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    override fun getItemCount() = books.size

    class Utils {

        companion object {
            fun startActivity(context: Context, clazz: Class<*>, bookId:Int) {

                val intent = Intent(context, clazz)
                intent.putExtra("BOOK_ID",bookId.toString())
                context.startActivity(intent)

            }
        }
    }

}