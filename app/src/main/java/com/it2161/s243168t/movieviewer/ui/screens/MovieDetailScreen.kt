package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail.MovieDetailViewModel

@Composable
fun MovieDetailScreen(navController: NavController, movieId: Int) {
    val viewModel: MovieDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back button
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading state
        if (uiState.isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        // Error state
        if (uiState.errorMessage != null) {
            Text(
                text = "Error: ${uiState.errorMessage}",
                color = MaterialTheme.colorScheme.error
            )
        }

        // Movie details
        val movie = uiState.movie
        if (movie != null) {
            Text("Movie ID: ${movie.id}", style = MaterialTheme.typography.bodySmall)
            Text("Title: ${movie.title}", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Release Date: ${movie.releaseDate}", style = MaterialTheme.typography.bodySmall)
            Text("Runtime: ${movie.runtime} minutes", style = MaterialTheme.typography.bodySmall)
            Text("Vote Average: ${movie.voteAverage}/10", style = MaterialTheme.typography.bodySmall)
            Text("Vote Count: ${movie.voteCount}", style = MaterialTheme.typography.bodySmall)
            Text("Language: ${movie.originalLanguage}", style = MaterialTheme.typography.bodySmall)
            Text("Adult: ${movie.adult}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Genres: ${movie.genres.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            Text("Revenue: ${movie.revenue}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Overview:", style = MaterialTheme.typography.titleSmall)
            Text(movie.overview, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Poster Path: ${movie.posterPath}", style = MaterialTheme.typography.bodySmall)
            Text("Backdrop Path: ${movie.backdropPath}", style = MaterialTheme.typography.bodySmall)
        } else {
            Text("No movie data available")
        }
    }
}
