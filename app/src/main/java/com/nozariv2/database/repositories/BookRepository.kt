package com.nozariv2.database.repositories

import androidx.lifecycle.LiveData
import com.nozariv2.database.daos.BookDoa
import com.nozariv2.database.tables.Book


class BookRepository(private  val bookDoa: BookDoa) {

    val allBooks: LiveData<List<Book>> = bookDoa.getAlphabetizedBooks()

    suspend fun insert(book: Book){
        bookDoa.insertBook(book)
    }

    fun getID(bookName:String):Int{
        return bookDoa.getID(bookName)
    }

    fun searchBooks(string:String):LiveData<List<Book>> {
        if (string.isEmpty()) return  bookDoa.getAlphabetizedBooks()
        return bookDoa.filterBooks("%$string%")
    }

}