package app.bilibili_m25.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object History : Screen("history")
    data object Settings : Screen("settings")
    data object Folder : Screen("folder")
    data object FolderDetail : Screen("folder_detail/{folderPath}") {
        fun createRoute(folderPath: String) = "folder_detail/${java.net.URLEncoder.encode(folderPath, "UTF-8")}"
    }
    data object VideoDetail : Screen("video_detail/{videoId}") {
        fun createRoute(videoId: Long) = "video_detail/$videoId"
    }
    data object VideoPlayer : Screen("video_player/{videoId}") {
        fun createRoute(videoId: Long) = "video_player/$videoId"
    }
}
