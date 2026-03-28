package app.bilibili_m25

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.bilibili_m25.data.local.ThemeMode
import app.bilibili_m25.data.local.ThemePreferences
import app.bilibili_m25.data.repository.PlayQueueManager
import app.bilibili_m25.ui.navigation.BilibiliNavHost
import app.bilibili_m25.ui.navigation.Screen
import app.bilibili_m25.ui.theme.BilibiliTheme
import app.bilibili_m25.util.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var themePreferences: ThemePreferences

    @Inject
    lateinit var playQueueManager: PlayQueueManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        logger.init(this)

        setContent {
            val themeMode by themePreferences.getThemeMode().collectAsState(initial = ThemeMode.SYSTEM)

            BilibiliTheme(themeMode = themeMode) {
                BilibiliAppContent(playQueueManager = playQueueManager)
            }
        }
    }
}

@Composable
fun BilibiliAppContent(playQueueManager: PlayQueueManager) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem("首页", Icons.Default.Home, Screen.Home.route),
        BottomNavItem("历史", Icons.Default.History, Screen.History.route),
        BottomNavItem("收藏", Icons.Default.Favorite, Screen.Favorites.route),
        BottomNavItem("设置", Icons.Default.Settings, Screen.Settings.route)
    )

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Favorites.route,
        Screen.Settings.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        BilibiliNavHost(
            navController = navController,
            paddingValues = paddingValues,
            playQueueManager = playQueueManager
        )
    }
}
