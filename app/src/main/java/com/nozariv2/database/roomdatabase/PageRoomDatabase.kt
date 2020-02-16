package com.nozariv2.database.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nozariv2.database.daos.PageDoa
import com.nozariv2.database.tables.Page
import kotlinx.coroutines.CoroutineScope

/**
 * The Room database that contains the books_table
 */

@Database(entities = arrayOf(Page::class),version = 1,exportSchema = false)
abstract class PageRoomDatabase : RoomDatabase(){

    abstract fun pageDoa(): PageDoa

    companion object {

        @Volatile
        private var INSTANCE: PageRoomDatabase? = null

        fun getDatabase(context: Context):PageRoomDatabase{
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PageRoomDatabase::class.java,
                    "page_database"
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