package com.it2161.s243168t.movieviewer.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.it2161.s243168t.movieviewer.data.local.models.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Upsert
    suspend fun upsertMovies(movies: List<Movie>)

    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE category = :category ORDER BY voteAverage DESC")
    fun getMoviesByCategory(category: String): Flow<List<Movie>>

    @Query("DELETE FROM movies WHERE id NOT IN (:favoriteIds)")
    suspend fun deleteNonFavorites(favoriteIds: List<Int>)

    @Query("SELECT * FROM movies WHERE id IN (:ids)")
    fun getMoviesByIds(ids: List<Int>): Flow<List<Movie>>

    @Transaction
    suspend fun transactionalRefresh(categoryMovies: List<Movie>, favoriteIds: List<Int>) {
        deleteNonFavorites(favoriteIds)
        upsertMovies(categoryMovies)
    }
}
