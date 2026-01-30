package com.it2161.s243168t.movieviewer.data.mappers

import com.it2161.s243168t.movieviewer.data.local.models.Movie
import com.it2161.s243168t.movieviewer.data.local.models.Review
import com.it2161.s243168t.movieviewer.data.remote.dtos.MovieDto
import com.it2161.s243168t.movieviewer.data.remote.dtos.ReviewDto

object MovieMapper {

    fun MovieDto.toMovie(): Movie {
        return Movie(
            id = id,
            adult = adult,
            genres = genres?.map { it.name } ?: emptyList(),
            originalLanguage = originalLanguage,
            title = title,
            releaseDate = releaseDate,
            runtime = runtime ?: 0,
            voteCount = voteCount,
            overview = overview,
            voteAverage = voteAverage,
            revenue = revenue ?: 0L,
            posterPath = posterPath ?: "",
            backdropPath = backdropPath ?: ""
        )
    }

    fun ReviewDto.toReview(movieId: Int): Review {
        return Review(
            movieId = movieId,
            author = author,
            content = content
        )
    }
}
