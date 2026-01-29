package com.it2161.s243168t.movieviewer.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.it2161.s243168t.movieviewer.data.local.models.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Upsert
    suspend fun upsertMovies(movies: List<Movie>)

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("DELETE FROM movies")
    suspend fun clearAllMovies()
}
