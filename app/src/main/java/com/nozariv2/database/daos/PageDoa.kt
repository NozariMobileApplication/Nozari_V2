package com.nozariv2.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nozariv2.database.tables.Page

@Dao
interface PageDoa{

    /**
     * Get list of pages.
     * @return list of pages for  the table in ascending order by sorting index.
     */
    @Query("SELECT * from pages_table  ORDER BY sorting_index ASC")
    fun getOrderedPages(): List<Page>

    /**
     * Get list of pages.
     * @return list of pages for specified book from the table in ascending order by sorting index.
     */
    @Query("SELECT * from pages_table WHERE book_id=:bookId  ORDER BY sorting_index ASC")
    fun filterPages(bookId:Int): List<Page>

    /**
     * Insert a page into the database. If the page already exists, replace it.
     * @param page the page to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPage(page: Page)

    /**
     * Delete all books.
     */
    @Query("DELETE FROM pages_table")
    fun deleteAllBooks()

}