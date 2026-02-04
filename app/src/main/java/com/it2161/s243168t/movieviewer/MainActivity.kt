package com.it2161.s243168t.movieviewer

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.it2161.s243168t.movieviewer.data.SessionManager
import com.it2161.s243168t.movieviewer.ui.navigation.NavGraph
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.theme._243168TMovieViewerTheme
import com.it2161.s243168t.movieviewer.utils.NetworkObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var networkObserver: NetworkObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContent {
            _243168TMovieViewerTheme {
                val userId by sessionManager.userId.collectAsState(initial = null)
                val isConnected by networkObserver.isConnected.collectAsState(initial = true)
                val startDestination = if (userId != null) Routes.MovieList.route else Routes.Login.route

                NavGraph(
                    modifier = Modifier.fillMaxSize(),
                    startDestination = startDestination,
                    isNetworkConnected = isConnected
                )
            }
        }
    }
}
