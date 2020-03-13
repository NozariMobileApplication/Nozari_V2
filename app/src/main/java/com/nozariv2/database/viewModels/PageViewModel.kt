package com.nozariv2.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nozariv2.database.repositories.PageRepository
import com.nozariv2.database.roomdatabase.PageRoomDatabase
import com.nozariv2.database.tables.Page
import kotlinx.coroutines.launch

class PageViewModel (application: Application) : AndroidViewModel(application){

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: PageRepository


    val allPages: MutableLiveData<List<Page>>


    init{

        val pageDoa = PageRoomDatabase.getDatabase(application).pageDoa()
        repository = PageRepository(pageDoa)
        allPages = repository.allPages
    }

        fun insert(page: Page) = viewModelScope.launch {
            repository.insert(page)
        }

        fun filter(bookId:Int) = viewModelScope.launch {
            allPages.value
        repository.filterPages(bookId)
    }

}