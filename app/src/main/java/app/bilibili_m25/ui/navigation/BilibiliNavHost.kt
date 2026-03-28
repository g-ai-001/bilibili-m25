package app.bilibili_m25.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import app.bilibili_m25.data.repository.PlayQueueManager
import app.bilibili_m25.ui.screen.home.HomeScreen
import app.bilibili_m25.ui.screen.video.VideoPlayerScreen
import app.bilibili_m25.ui.screen.search.SearchScreen
import app.bilibili_m25.ui.screen.settings.SettingsScreen

@Composable
fun BilibiliNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    playQueueManager: PlayQueueManager,
    modifier: Modifier = Modifier
) {
    val playQueue by playQueueManager.playQueue.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onVideoClick = { videoId ->
                    val videos = playQueue.ifEmpty { emptyList() }
                    val index = videos.indexOfFirst { it.id == videoId }
                    if (videos.isEmpty() || index == -1) {
                        navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                    } else {
                        navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                    }
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onVideoClick = { videoId ->
                    navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Favorites.route) {
            HomeScreen(
                onVideoClick = { videoId ->
                    navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                showFavoritesOnly = true
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(
            route = Screen.VideoPlayer.route,
            arguments = listOf(
                navArgument("videoId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getLong("videoId") ?: 0L
            VideoPlayerScreen(
                videoId = videoId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
