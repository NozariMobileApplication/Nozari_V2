package com.nozariv2.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "books_table")
class Book (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "book_id")
    val bookId: Int,
//    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "userid")
    val userId: String,

    @ColumnInfo(name = "create_date")
    val createDate: String,

    @ColumnInfo(name = "name")
    val bookName: String,

    @ColumnInfo(name = "uri")
    val uri: String

)