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
import app.bilibili_m25.ui.screen.history.HistoryScreen
import app.bilibili_m25.ui.screen.folder.FolderScreen
import app.bilibili_m25.ui.screen.folder.FolderDetailScreen
import app.bilibili_m25.ui.screen.detail.VideoDetailScreen

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
                    navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onFolderClick = { folderPath ->
                    navController.navigate(Screen.FolderDetail.createRoute(folderPath))
                },
                onFoldersClick = {
                    navController.navigate(Screen.Folder.route)
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

        composable(Screen.History.route) {
            HistoryScreen(
                onVideoClick = { videoId ->
                    navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen()
        }

        composable(Screen.Folder.route) {
            FolderScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onFolderClick = { folderPath ->
                    navController.navigate(Screen.FolderDetail.createRoute(folderPath))
                }
            )
        }

        composable(
            route = Screen.FolderDetail.route,
            arguments = listOf(
                navArgument("folderPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val folderPath = backStackEntry.arguments?.getString("folderPath") ?: ""
            val decodedPath = java.net.URLDecoder.decode(folderPath, "UTF-8")
            FolderDetailScreen(
                folderPath = decodedPath,
                onBackClick = {
                    navController.popBackStack()
                },
                onVideoClick = { videoId ->
                    navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                }
            )
        }

        composable(
            route = Screen.VideoDetail.route,
            arguments = listOf(
                navArgument("videoId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getLong("videoId") ?: 0L
            VideoDetailScreen(
                videoId = videoId,
                onBackClick = {
                    navController.popBackStack()
                },
                onPlayClick = { id ->
                    navController.navigate(Screen.VideoPlayer.createRoute(id))
                }
            )
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
                },
                onDetailClick = {
                    navController.navigate(Screen.VideoDetail.createRoute(videoId))
                }
            )
        }
    }
}
