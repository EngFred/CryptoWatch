package com.engfred.cryptowatch.ui.nav

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.engfred.cryptowatch.ui.detail.CryptoDetailScreen
import com.engfred.cryptowatch.ui.list.CryptoListScreen

@Composable
fun CryptoAppNavHost() {
    val navController = rememberNavController()

    val animationDuration = 300

    NavHost(
        navController = navController,
        startDestination = Screen.CryptoList.route,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {

        composable(
            route = Screen.CryptoList.route,
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            }
        ) {
            CryptoListScreen(
                onCoinClick = { id ->
                    navController.navigate(Screen.CryptoDetail(id).createRoute())
                }
            )
        }

        composable(
            route = Screen.CryptoDetail.routeWithArgs,
            arguments = listOf(
                navArgument(Screen.CryptoDetail.coinIdArg) { type = NavType.StringType }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(animationDuration)
                )
            }
        ) { backStackEntry ->
            CryptoDetailScreen(
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}