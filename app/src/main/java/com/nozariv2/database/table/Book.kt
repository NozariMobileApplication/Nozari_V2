package com.nozariv2.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity (tableName = "books_table")
class Book (

    @PrimaryKey()
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "userid")
    val userId: String,

    @ColumnInfo(name = "name")
    val bookName: String
)