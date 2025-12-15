package com.engfred.cryptowatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.engfred.cryptowatch.ui.detail.CryptoDetailScreen
import com.engfred.cryptowatch.ui.list.CryptoListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoAppNavHost()
        }
    }
}

@Composable
fun CryptoAppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            CryptoListScreen(onCoinClick = { id -> navController.navigate("detail/$id") })
        }
        composable(
            route = "detail/{coinId}",
            arguments = listOf(navArgument("coinId") { type = NavType.StringType })
        ) {
            CryptoDetailScreen()
        }
    }
}