package com.it2161.s243168t.movieviewer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.it2161.s243168t.movieviewer.data.local.converters.GenreConverters
import com.it2161.s243168t.movieviewer.data.local.converters.DateConverters
import com.it2161.s243168t.movieviewer.data.local.daos.MovieDao
import com.it2161.s243168t.movieviewer.data.local.daos.ReviewDao
import com.it2161.s243168t.movieviewer.data.local.daos.UserDao
import com.it2161.s243168t.movieviewer.data.local.models.Movie
import com.it2161.s243168t.movieviewer.data.local.models.Review
import com.it2161.s243168t.movieviewer.data.local.models.User

@Database(entities = [User::class, Movie::class, Review::class], version = 2, exportSchema = false)
@TypeConverters(DateConverters::class, GenreConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun movieDao(): MovieDao
    abstract fun reviewDao(): ReviewDao
}