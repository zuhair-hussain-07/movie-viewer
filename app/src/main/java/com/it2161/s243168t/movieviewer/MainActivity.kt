package com.it2161.s243168t.movieviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        enableEdgeToEdge()
        setContent {
            _243168TMovieViewerTheme {
                val userId by sessionManager.userId.collectAsState(initial = null)
                val isConnected by networkObserver.isConnected.collectAsState(initial = true)
                val startDestination = if (userId != null) Routes.MovieList.route else Routes.Login.route

                val snackbarHostState = remember { SnackbarHostState() }
                var wasOffline by remember { mutableStateOf(false) }

                // React to network status changes
                LaunchedEffect(isConnected) {
                    if (!isConnected) {
                        wasOffline = true
                        snackbarHostState.showSnackbar(
                            message = "No internet connection",
                            duration = SnackbarDuration.Indefinite
                        )
                    } else if (wasOffline) {
                        // Dismiss any existing snackbar and show "Back online"
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = "Back online",
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    NavGraph(
                        startDestination = startDestination,
                        isNetworkConnected = isConnected
                    )

                    // Global network snackbar overlaid at the bottom
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 96.dp) // Offset to appear above bottom nav bar
                    ) { data ->
                        Snackbar(snackbarData = data)
                    }
                }
            }
        }
    }
}
