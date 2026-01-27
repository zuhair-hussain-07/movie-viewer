package com.it2161.s243168t.movieviewer.ui.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object ViewProfile : Routes("view_profile")
    object EditProfile : Routes("edit_profile")
    object MovieList : Routes("movie_list")
    object MovieDetail : Routes("movie_detail/{movieId}") {
        fun createRoute(movieId: Int) = "movie_detail/$movieId"
    }
    object Favorites : Routes("favorites")
    object Search : Routes("search")
}