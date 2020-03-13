package com.nozariv2.database.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nozariv2.database.daos.BookDoa
import com.nozariv2.database.tables.Book
import kotlinx.coroutines.CoroutineScope

/**
 * The Room database that contains the books_table
 */

@Database(entities = arrayOf(Book::class),version = 1,exportSchema = false)
abstract class BookRoomDatabase : RoomDatabase(){

    abstract fun bookDoa(): BookDoa

    companion object {

        @Volatile
        private var INSTANCE: BookRoomDatabase? = null

        fun getDatabase(context: Context):BookRoomDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookRoomDatabase::class.java,
                    "book_database"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() // REMOVE THIS FOR LIVE VERSION
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }

}