package com.it2161.s243168t.movieviewer.ui.viewmodels.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.s243168t.movieviewer.data.repositories.MovieRepository
import com.it2161.s243168t.movieviewer.utils.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val networkObserver: NetworkObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<MovieUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        // Observe network connectivity
        observeNetworkStatus()

        // Load initial category (popular)
        loadMovies(_uiState.value.selectedCategory)

        // Setup search debounce
        setupSearchDebounce()
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkObserver.isConnected.collect { isConnected ->
                _uiState.update { it.copy(isOnline = isConnected) }
            }
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _uiState
                .map { it.searchQuery }
                .distinctUntilChanged()
                .debounce(500L)
                .filter { it.isNotBlank() }
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    fun onEvent(event: MovieUiEvent) {
        when (event) {
            is MovieUiEvent.OnCategoryChanged -> handleCategoryChange(event.category)
            is MovieUiEvent.OnSearchQueryChanged -> handleSearchQueryChange(event.query)
            is MovieUiEvent.OnMovieClicked -> handleMovieClicked(event.movieId)
            MovieUiEvent.RefreshList -> handleRefresh()
        }
    }

    private fun handleCategoryChange(category: String) {
        viewModelScope.launch {
            val isOnline = networkObserver.isConnected.firstOrNull() ?: false

            // Check if trying to switch categories while offline
            if (!isOnline && category != _uiState.value.selectedCategory) {
                emitEffect(MovieUiEffect.ShowSnackbar("Feature unavailable offline"))
                return@launch
            }

            // Clear search query when switching to category
            _uiState.update {
                it.copy(
                    selectedCategory = category,
                    searchQuery = ""
                )
            }

            loadMovies(category)
        }
    }

    private fun handleSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // If query is empty, reload the current category
        if (query.isBlank()) {
            loadMovies(_uiState.value.selectedCategory)
        }
        // Otherwise, the debounce will trigger the search
    }

    private fun handleMovieClicked(movieId: Int) {
        viewModelScope.launch {
            emitEffect(MovieUiEffect.NavigateToDetail(movieId))
        }
    }

    private fun handleRefresh() {
        val currentQuery = _uiState.value.searchQuery
        if (currentQuery.isBlank()) {
            loadMovies(_uiState.value.selectedCategory)
        } else {
            performSearch(currentQuery)
        }
    }

    private fun loadMovies(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                movieRepository.getMovies(category).collect { movies ->
                    _uiState.update {
                        it.copy(
                            movies = movies,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                emitEffect(MovieUiEffect.ShowSnackbar("Error loading movies: ${e.message}"))
                e.printStackTrace()
            }
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                movieRepository.searchMovies(query).collect { movies ->
                    _uiState.update {
                        it.copy(
                            movies = movies,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                emitEffect(MovieUiEffect.ShowSnackbar("Error searching movies: ${e.message}"))
                e.printStackTrace()
            }
        }
    }

    private suspend fun emitEffect(effect: MovieUiEffect) {
        _uiEffect.emit(effect)
    }
}
