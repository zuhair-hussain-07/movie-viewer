package com.it2161.s243168t.movieviewer.ui.viewmodels.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.s243168t.movieviewer.data.local.models.Movie
import com.it2161.s243168t.movieviewer.data.repositories.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<FavoritesUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        loadFavorites()
        observeFavoriteIds()
    }

    private fun observeFavoriteIds() {
        viewModelScope.launch {
            movieRepository.getFavouriteIds().collect { favoriteIds ->
                _uiState.update { it.copy(favoriteIds = favoriteIds) }
                // Reload favorites when IDs change
                loadFavorites()
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                movieRepository.getFavouritedMovies().collect { movies ->
                    _uiState.update {
                        it.copy(
                            movies = movies,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading favorites: ${e.message}"
                    )
                }
                e.printStackTrace()
            }
        }
    }

    fun onEvent(event: FavoritesUiEvent) {
        when (event) {
            is FavoritesUiEvent.OnMovieClicked -> handleMovieClicked(event.movieId)
            is FavoritesUiEvent.ToggleFavorite -> handleToggleFavorite(event.movie)
            FavoritesUiEvent.RefreshList -> loadFavorites()
        }
    }

    private fun handleMovieClicked(movieId: Int) {
        viewModelScope.launch {
            emitEffect(FavoritesUiEffect.NavigateToDetail(movieId))
        }
    }

    private fun handleToggleFavorite(movie: Movie) {
        viewModelScope.launch {
            try {
                movieRepository.toggleFavourite(movie.id)
                val message = "${movie.title} removed from favorites"
                emitEffect(FavoritesUiEffect.ShowSnackbar(message))
            } catch (e: Exception) {
                emitEffect(FavoritesUiEffect.ShowSnackbar("Error updating favorites"))
                e.printStackTrace()
            }
        }
    }

    private suspend fun emitEffect(effect: FavoritesUiEffect) {
        _uiEffect.emit(effect)
    }
}
