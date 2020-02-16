package com.nozariv2.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nozariv2.database.tables.Book

@Dao
interface BookDoa{

    /**
     * Get list of books.
     * @return list of books from the table in ascending order by name.
     */
    @Query("SELECT * from books_table ORDER BY name ASC")
    fun getAlphabetizedBooks(): LiveData<List<Book>>

    /**
     * Insert a book into the database. If the book already exists, replace it.
     * @param book the book to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: Book)

    /**
     * Delete all books.
     */
    @Query("DELETE FROM books_table")
    fun deleteAllBooks()

}