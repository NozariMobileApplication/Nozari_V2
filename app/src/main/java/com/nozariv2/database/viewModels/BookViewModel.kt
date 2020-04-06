package com.nozariv2.database.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText.Word
import com.nozariv2.database.repositories.BookRepository
import com.nozariv2.database.roomdatabase.BookRoomDatabase
import com.nozariv2.database.tables.Book
import kotlinx.coroutines.launch


class BookViewModel (application: Application) : AndroidViewModel(application){

    // Gets reference to BookDoa from BookRoomDatabase to construct
    // the correct BookRepository.
    val booksDoa = BookRoomDatabase.getDatabase(application).bookDoa()

    // The ViewModel maintains a reference to the repository to get data.
    private val repository:BookRepository = BookRepository(booksDoa)

    // Filter Books
    private val _searchStringLiveData = MutableLiveData<String>()
    val filteredBooks = Transformations.switchMap(_searchStringLiveData){string->
        repository.searchBooks(string)
    }


    // LiveData gives us updated books when they change.
    val allbooks: LiveData<List<Book>>

    init{

        // Default when not wanting to filter
        // allbooks = repository.allBooks
        allbooks = filteredBooks

        // Filter Books
        _searchStringLiveData.value=""

    }

    fun insert(book: Book) = viewModelScope.launch {
        repository.insert(book)
    }

    fun searchNameChanged(name:String){
        _searchStringLiveData.value=name
    }

    fun getId(bookname:String):Int{
        return repository.getID(bookname)
    }


}