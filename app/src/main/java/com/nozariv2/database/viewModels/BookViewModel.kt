package com.nozariv2.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.nozariv2.database.repositories.BookRepository
import com.nozariv2.database.roomdatabase.BookRoomDatabase
import com.nozariv2.database.tables.Book
import kotlinx.coroutines.launch

class BookViewModel (application: Application) : AndroidViewModel(application){

    // The ViewModel maintains a reference to the repository to get data.
    private val repository:BookRepository

    // LiveData gives us updated books when they change.
    val allbooks: LiveData<List<Book>>

    init{
        // Gets reference to BookDoa from BookRoomDatabase to construct
        // the correct BookRepository.
        val booksDoa = BookRoomDatabase.getDatabase(application).bookDoa()
        repository = BookRepository(booksDoa)
        allbooks = repository.allBooks
    }

        fun insert(book: Book) = viewModelScope.launch {
            repository.insert(book)
        }

}