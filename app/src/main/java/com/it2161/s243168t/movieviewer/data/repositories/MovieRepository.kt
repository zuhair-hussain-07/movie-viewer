package com.it2161.s243168t.movieviewer.data.repositories

import com.it2161.s243168t.movieviewer.data.local.daos.MovieDao
import com.it2161.s243168t.movieviewer.data.local.daos.ReviewDao
import com.it2161.s243168t.movieviewer.data.local.datastore.FavouritesDataStore
import com.it2161.s243168t.movieviewer.data.local.models.Movie
import com.it2161.s243168t.movieviewer.data.local.models.Review
import com.it2161.s243168t.movieviewer.data.mappers.MovieMapper.toMovie
import com.it2161.s243168t.movieviewer.data.mappers.MovieMapper.toReview
import com.it2161.s243168t.movieviewer.data.remote.api.TMDBApiService
import com.it2161.s243168t.movieviewer.utils.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val tmdbApiService: TMDBApiService,
    private val movieDao: MovieDao,
    private val reviewDao: ReviewDao,
    private val networkObserver: NetworkObserver,
    private val favouritesDataStore: FavouritesDataStore
) {

    fun getMovies(category: String): Flow<List<Movie>> = flow {
        // Emit initial cached value for this specific category
        val cachedMovies = movieDao.getMoviesByCategory(category).firstOrNull() ?: emptyList()
        emit(cachedMovies)

        // Check if network is available
        val isOnline = networkObserver.isConnected.firstOrNull() ?: false
        if (isOnline) {
            try {
                // Fetch data from API first
                val apiResponse = when (category) {
                    "popular" -> tmdbApiService.getPopularMovies()
                    "top_rated" -> tmdbApiService.getTopRatedMovies()
                    "now_playing" -> tmdbApiService.getNowPlayingMovies()
                    "upcoming" -> tmdbApiService.getUpcomingMovies()
                    else -> return@flow
                }

                // Get current favorite IDs to preserve them during refresh
                val favoriteIds = favouritesDataStore.getFavouriteIds().first()
                    .mapNotNull { it.toIntOrNull() }

                // Map API results to Movie entities
                val movies = apiResponse.results.map { it.toMovie(category) }

                // Use transactional refresh for atomic database update (no UI flicker)
                withContext(Dispatchers.IO) {
                    movieDao.transactionalRefresh(movies, favoriteIds)
                }

                // Emit all updates from DAO's Flow for this category
                emitAll(movieDao.getMoviesByCategory(category))
            } catch (e: Exception) {
                // On error, keep showing cached data (already emitted)
                e.printStackTrace()
            }
        }
    }

    fun getMovieDetails(movieId: Int): Flow<Movie?> = flow {
        // Emit initial cached value
        val cachedMovies = movieDao.getAllMovies().firstOrNull() ?: emptyList()
        val existingMovie = cachedMovies.find { it.id == movieId }
        emit(existingMovie)

        // Check if network is available
        val isOnline = networkObserver.isConnected.firstOrNull() ?: false
        if (isOnline) {
            try {
                // The getMovieDetails endpoint returns a single MovieDto object
                val movieDto = tmdbApiService.getMovieDetails(movieId)
                // Preserve the existing category if movie was already in DB
                val movie = movieDto.toMovie(existingMovie?.category ?: "")

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
        val searchCategory = "search"

        // Emit initial cached value for search results
        val cachedMovies = movieDao.getMoviesByCategory(searchCategory).firstOrNull() ?: emptyList()
        emit(cachedMovies)

        // Check if network is available
        val isOnline = networkObserver.isConnected.firstOrNull() ?: false
        if (isOnline) {
            try {
                // Fetch data from API first
                val apiResponse = tmdbApiService.searchMovies(query)

                // Get current favorite IDs to preserve them during refresh
                val favoriteIds = favouritesDataStore.getFavouriteIds().first()
                    .mapNotNull { it.toIntOrNull() }

                // Map API results to Movie entities
                val movies = apiResponse.results.map { it.toMovie(searchCategory) }

                // Use transactional refresh for atomic database update (no UI flicker)
                withContext(Dispatchers.IO) {
                    movieDao.transactionalRefresh(movies, favoriteIds)
                }

                // Emit all updates from DAO's Flow for search category
                emitAll(movieDao.getMoviesByCategory(searchCategory))
            } catch (e: Exception) {
                // On error, keep showing cached data (already emitted)
                e.printStackTrace()
            }
        }
        // If offline, cached data is already emitted - user sees last successful results
    }

    fun getFavouriteIds(): Flow<Set<Int>> {
        return favouritesDataStore.getFavouriteIds().map { ids ->
            ids.mapNotNull { it.toIntOrNull() }.toSet()
        }
    }

    suspend fun toggleFavourite(movieId: Int) {
        favouritesDataStore.toggleFavourite(movieId)
    }

    fun isMovieFavourited(movieId: Int): Flow<Boolean> {
        return favouritesDataStore.getFavouriteIds().map { ids ->
            ids.contains(movieId.toString())
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun getFavouritedMovies(): Flow<List<Movie>> = favouritesDataStore.getFavouriteIds()
        .flatMapLatest { idsSet ->
            val favoriteIds = idsSet.mapNotNull { it.toIntOrNull() }

            if (favoriteIds.isEmpty()) {
                flowOf(emptyList())
            } else {
                flow {
                    // Get movies from local database
                    val localMovies = movieDao.getMoviesByIds(favoriteIds).firstOrNull() ?: emptyList()
                    val localMovieIds = localMovies.map { it.id }.toSet()

                    // Find missing movie IDs (in DataStore but not in Room)
                    val missingIds = favoriteIds.filter { it !in localMovieIds }

                    if (missingIds.isNotEmpty()) {
                        // Check if network is available to fetch missing movies
                        val isOnline = networkObserver.isConnected.firstOrNull() ?: false
                        if (isOnline) {
                            try {
                                // Fetch missing movies from API and upsert them with "favorites" category
                                val fetchedMovies = missingIds.mapNotNull { movieId ->
                                    try {
                                        val movieDto = tmdbApiService.getMovieDetails(movieId)
                                        movieDto.toMovie("favorites")
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        null
                                    }
                                }

                                if (fetchedMovies.isNotEmpty()) {
                                    withContext(Dispatchers.IO) {
                                        movieDao.upsertMovies(fetchedMovies)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    // Emit all updates from DAO's Flow
                    emitAll(movieDao.getMoviesByIds(favoriteIds))
                }
            }
        }
}
