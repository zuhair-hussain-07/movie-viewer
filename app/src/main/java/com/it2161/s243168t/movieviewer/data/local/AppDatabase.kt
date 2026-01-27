package com.it2161.s243168t.movieviewer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.it2161.s243168t.movieviewer.data.local.converters.DateConverters
import com.it2161.s243168t.movieviewer.data.local.daos.UserDao
import com.it2161.s243168t.movieviewer.data.local.models.User

@Database(entities = [User::class], version = 1, exportSchema = false)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}