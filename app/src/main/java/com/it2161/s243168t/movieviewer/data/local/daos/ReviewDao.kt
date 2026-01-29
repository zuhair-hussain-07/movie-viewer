package com.it2161.s243168t.movieviewer.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.it2161.s243168t.movieviewer.data.local.models.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {

    @Upsert
    suspend fun upsertReviews(reviews: List<Review>)

    @Query("SELECT * FROM reviews WHERE movieId = :movieId")
    fun getReviewsForMovie(movieId: Int): Flow<List<Review>>
}
