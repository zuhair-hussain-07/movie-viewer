package com.it2161.s243168t.movieviewer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.it2161.s243168t.movieviewer.ui.screens.FavoritesScreen
import com.it2161.s243168t.movieviewer.ui.screens.LoginScreen
import com.it2161.s243168t.movieviewer.ui.screens.MovieDetailScreen
import com.it2161.s243168t.movieviewer.ui.screens.MovieListScreen
import com.it2161.s243168t.movieviewer.ui.screens.ProfileScreen
import com.it2161.s243168t.movieviewer.ui.screens.RegistrationScreen
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    startDestination: String = Routes.Register.route,
    isNetworkConnected: Boolean
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.Login.route) { LoginScreen(navController = navController) }
        composable(Routes.Register.route) {
            RegistrationScreen(navController = navController)
        }
        composable(Routes.Profile.route) { ProfileScreen(navController = navController) }
        composable(Routes.MovieList.route) { MovieListScreen(navController = navController) }
        composable(
            route = Routes.MovieDetail.route,
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            MovieDetailScreen(navController = navController, movieId = -1)
        }
        composable(Routes.Favorites.route) { FavoritesScreen(navController = navController) }
    }
}
