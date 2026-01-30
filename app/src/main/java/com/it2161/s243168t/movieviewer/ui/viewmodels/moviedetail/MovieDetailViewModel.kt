package com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.s243168t.movieviewer.data.repositories.MovieRepository
import com.it2161.s243168t.movieviewer.utils.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val networkObserver: NetworkObserver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MovieDetailUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    // Retrieve movieId from navigation arguments
    private val movieId: Int = savedStateHandle.get<Int>("movieId") ?: -1

    init {
        observeNetworkStatus()

        // Load movie details if movieId is valid
        if (movieId > 0) {
            loadMovieDetail(movieId)
        } else {
            viewModelScope.launch {
                emitEffect(MovieDetailUiEffect.ShowToast("Invalid movie ID"))
            }
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkObserver.isConnected.collect { isConnected ->
                _uiState.value = _uiState.value.copy(isOnline = isConnected)
            }
        }
    }

    private fun loadMovieDetail(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                // Collect movie details
                movieRepository.getMovieDetails(movieId).collect { movie ->
                    _uiState.value = _uiState.value.copy(
                        movie = movie,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)

                // Check if there's cached data
                if (_uiState.value.movie == null) {
                    emitEffect(MovieDetailUiEffect.ShowToast("Error loading movie details: ${e.message}"))
                }
                e.printStackTrace()
            }
        }

        // Load reviews for the movie
        viewModelScope.launch {
            try {
                movieRepository.getReviews(movieId).collect { reviews ->
                    _uiState.value = _uiState.value.copy(reviews = reviews)
                }
            } catch (e: Exception) {
                // If reviews fail to load, still show the movie with empty reviews
                e.printStackTrace()
            }
        }
    }

    fun onEvent(event: MovieDetailUiEvent) {
        when (event) {
            is MovieDetailUiEvent.LoadMovieDetail -> handleLoadMovieDetail(event.movieId)
            is MovieDetailUiEvent.ToggleFavorite -> handleToggleFavorite(event.movieId)
            MovieDetailUiEvent.OnBackClicked -> handleBackClicked()
        }
    }

    private fun handleLoadMovieDetail(movieId: Int) {
        loadMovieDetail(movieId)
    }

    private fun handleToggleFavorite(@Suppress("UNUSED_PARAMETER") movieId: Int) {
        viewModelScope.launch {
            // TODO: Implement favorite toggling logic (save to local preferences/db)
            emitEffect(MovieDetailUiEffect.ShowToast("Movie added to favorites"))
        }
    }

    private fun handleBackClicked() {
        viewModelScope.launch {
            emitEffect(MovieDetailUiEffect.NavigateBack)
        }
    }

    private suspend fun emitEffect(effect: MovieDetailUiEffect) {
        _uiEffect.emit(effect)
    }
}
