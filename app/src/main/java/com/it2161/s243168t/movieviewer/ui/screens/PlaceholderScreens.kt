package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun MovieListScreen(navController: NavController) {
    Text("Movie List Screen")
}

@Composable
fun MovieDetailScreen(navController: NavController, movieId: Int) {
    Text("Movie Detail Screen for movieId: $movieId")
}

@Composable
fun FavoritesScreen(navController: NavController) {
    Text("Favorites Screen")
}

@Composable
fun SearchScreen(navController: NavController) {
    Text("Search Screen")
}
