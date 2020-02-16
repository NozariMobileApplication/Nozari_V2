package com.nozariv2.database.repositories

import androidx.lifecycle.LiveData
import com.nozariv2.books.Books
import com.nozariv2.database.daos.BookDoa
import com.nozariv2.database.tables.Book

class BookRepository(private  val bookDoa: BookDoa) {

    val allBooks: LiveData<List<Book>> = bookDoa.getAlphabetizedBooks()

    suspend fun insert(book: Book){
        bookDoa.insertBook(book)
    }
}