package com.nozariv2.database.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nozariv2.database.daos.PageDoa
import com.nozariv2.database.tables.Book
import com.nozariv2.database.tables.Page

class PageRepository(private val pageDoa: PageDoa) {

//    var data: LiveData<List<Page>> = pageDoa.getOrderedPages()

    var allPages: MutableLiveData<List<Page>> = MutableLiveData<List<Page>>(pageDoa.getOrderedPages())


    suspend fun insert(page: Page){
        pageDoa.insertPage(page)
    }

    fun filterPages(bookId:Int){
        allPages.setValue(pageDoa.filterPages(bookId))
    }
}