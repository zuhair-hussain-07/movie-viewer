package com.it2161.s243168t.movieviewer.data.repositories

import com.it2161.s243168t.movieviewer.data.local.daos.MovieDao
import com.it2161.s243168t.movieviewer.data.local.daos.ReviewDao
import com.it2161.s243168t.movieviewer.data.local.models.Movie
import com.it2161.s243168t.movieviewer.data.local.models.Review
import com.it2161.s243168t.movieviewer.data.mappers.MovieMapper.toMovie
import com.it2161.s243168t.movieviewer.data.mappers.MovieMapper.toReview
import com.it2161.s243168t.movieviewer.data.remote.api.TMDBApiService
import com.it2161.s243168t.movieviewer.utils.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val tmdbApiService: TMDBApiService,
    private val movieDao: MovieDao,
    private val reviewDao: ReviewDao,
    private val networkObserver: NetworkObserver
) {

    fun getMovies(category: String): Flow<List<Movie>> = flow {
        // Emit initial cached value
        val cachedMovies = movieDao.getAllMovies().firstOrNull() ?: emptyList()
        emit(cachedMovies)

        // Check if network is available
        val isOnline = networkObserver.isConnected.firstOrNull() ?: false
        if (isOnline) {
            try {
                val apiResponse = when (category) {
                    "popular" -> tmdbApiService.getPopularMovies()
                    "top_rated" -> tmdbApiService.getTopRatedMovies()
                    "now_playing" -> tmdbApiService.getNowPlayingMovies()
                    "upcoming" -> tmdbApiService.getUpcomingMovies()
                    else -> return@flow
                }

                // Clear old movies and save new ones
                withContext(Dispatchers.IO) {
                    movieDao.clearAllMovies()
                    val movies = apiResponse.results.map { it.toMovie() }
                    movieDao.upsertMovies(movies)
                }

                // Emit all updates from DAO's Flow
                emitAll(movieDao.getAllMovies())
            } catch (e: Exception) {
                // On error, keep showing cached data (already emitted)
                e.printStackTrace()
            }
        }
    }

    fun getMovieDetails(movieId: Int): Flow<Movie?> = flow {
        // Emit initial cached value
        val cachedMovies = movieDao.getAllMovies().firstOrNull() ?: emptyList()
        emit(cachedMovies.find { it.id == movieId })

        // Check if network is available
        val isOnline = networkObserver.isConnected.firstOrNull() ?: false
        if (isOnline) {
            try {
                // The getMovieDetails endpoint returns a single MovieDto object
                val movieDto = tmdbApiService.getMovieDetails(movieId)
                val movie = movieDto.toMovie()

                // Upsert the updated movie to DB
                withContext(Dispatchers.IO) {
                    movieDao.upsertMovies(listOf(movie))
                }

                // Emit the updated movie
                emit(movie)
            } catch (e: Exception) {
                // On error, keep showing cached data (already emitted)
                e.printStackTrace()
            }
        }
    }

    fun getReviews(movieId: Int): Flow<List<Review>> = flow {
        // Emit initial cached value
        val cachedReviews = reviewDao.getReviewsForMovie(movieId).firstOrNull() ?: emptyList()
        emit(cachedReviews)

        // Check if network is available
        val isOnline = networkObserver.isConnected.firstOrNull() ?: false
        if (isOnline) {
            try {
                val apiResponse = tmdbApiService.getMovieReviews(movieId)
                val reviews = apiResponse.results.map { it.toReview(movieId) }

                // Save reviews to DB
                withContext(Dispatchers.IO) {
                    reviewDao.upsertReviews(reviews)
                }

                // Emit all updates from DAO's Flow
                emitAll(reviewDao.getReviewsForMovie(movieId))
            } catch (e: Exception) {
                // On error, keep showing cached data (already emitted)
                e.printStackTrace()
            }
        }
    }

    fun searchMovies(query: String): Flow<List<Movie>> = flow {
        // Emit initial cached value (last search/category results)
        val cachedMovies = movieDao.getAllMovies().firstOrNull() ?: emptyList()
        emit(cachedMovies)

        // Check if network is available
        val isOnline = networkObserver.isConnected.firstOrNull() ?: false
        if (isOnline) {
            try {
                val apiResponse = tmdbApiService.searchMovies(query)

                // Clear old cache and save new search results
                withContext(Dispatchers.IO) {
                    movieDao.clearAllMovies()
                    val movies = apiResponse.results.map { it.toMovie() }
                    movieDao.upsertMovies(movies)
                }

                // Emit all updates from DAO's Flow
                emitAll(movieDao.getAllMovies())
            } catch (e: Exception) {
                // On error, keep showing cached data (already emitted)
                e.printStackTrace()
            }
        }
        // If offline, cached data is already emitted - user sees last successful results
    }
}
