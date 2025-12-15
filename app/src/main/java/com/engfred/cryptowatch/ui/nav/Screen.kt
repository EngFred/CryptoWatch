package com.engfred.cryptowatch.ui.nav

sealed class Screen(val route: String) {
    object CryptoList : Screen("list")

    data class CryptoDetail(val coinId: String) : Screen("detail/{coinId}") {
        fun createRoute(): String = "detail/$coinId"

        companion object {
            const val routeWithArgs: String = "detail/{coinId}"
            const val coinIdArg: String = "coinId"
        }
    }
}