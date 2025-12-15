package com.engfred.cryptowatch.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApi {
    @GET("coins/markets")
    suspend fun getCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = true,
        @Query("ids") ids: String? = null
    ): List<CoinDto>

    @GET("search")
    suspend fun searchGlobal(@Query("query") query: String): SearchResponseDto
}