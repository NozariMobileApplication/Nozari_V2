package com.nozariv2.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URI
import java.util.*

@Entity(tableName = "pages_table")
class Page (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "book_id")
    val bookId: Int,

    @ColumnInfo(name = "create_date")
    val createDate: String,

    @ColumnInfo(name = "uri")
    val uri: String,

    @ColumnInfo(name = "sorting_index")
    val sortingIndex: Int

)