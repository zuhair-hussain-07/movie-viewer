package com.it2161.s243168t.movieviewer.ui.viewmodels.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.s243168t.movieviewer.data.local.models.Movie
import com.it2161.s243168t.movieviewer.data.repositories.MovieRepository
import com.it2161.s243168t.movieviewer.utils.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val networkObserver: NetworkObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<FavoritesUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        observeNetworkStatus()
        observeFavorites()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkObserver.isConnected.collect { isConnected ->
                _uiState.update { it.copy(isOnline = isConnected) }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            // Observe both favorite IDs and movies reactively
            movieRepository.getFavouriteIds().collect { favoriteIds ->
                _uiState.update { it.copy(favoriteIds = favoriteIds) }
            }
        }

        viewModelScope.launch {
            movieRepository.getFavouritedMovies()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error loading favorites: ${e.message}"
                        )
                    }
                    e.printStackTrace()
                }
                .collect { movies ->
                    _uiState.update {
                        it.copy(
                            movies = movies,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun onEvent(event: FavoritesUiEvent) {
        when (event) {
            is FavoritesUiEvent.OnMovieClicked -> handleMovieClicked(event.movieId)
            is FavoritesUiEvent.ToggleFavorite -> handleToggleFavorite(event.movie)
            FavoritesUiEvent.RefreshList -> { /* No-op, flow is reactive */ }
        }
    }

    private fun handleMovieClicked(movieId: Int) {
        viewModelScope.launch {
            // Check if online before allowing navigation to details
            if (_uiState.value.isOnline) {
                emitEffect(FavoritesUiEffect.NavigateToDetail(movieId))
            } else {
                emitEffect(FavoritesUiEffect.ShowSnackbar("Cannot view details while offline"))
            }
        }
    }

    private fun handleToggleFavorite(movie: Movie) {
        viewModelScope.launch {
            try {
                movieRepository.toggleFavourite(movie.id)
                val message = "${movie.title} removed from favorites"
                emitEffect(FavoritesUiEffect.ShowSnackbar(message))
                // No need to reload - the flow is reactive and will update automatically
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
