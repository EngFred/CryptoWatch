package com.engfred.cryptowatch.data.remote

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("coins") val coins: List<SearchCoinDto>
)

data class SearchCoinDto(
    val id: String,
    val name: String,
    val symbol: String
)

data class CoinDto(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    @SerializedName("current_price") val currentPrice: Double,
    @SerializedName("price_change_percentage_24h") val priceChangePercentage24h: Double,
    @SerializedName("high_24h") val high24h: Double,
    @SerializedName("low_24h") val low24h: Double,
    @SerializedName("market_cap") val marketCap: Double,
    @SerializedName("total_volume") val totalVolume: Double,
    @SerializedName("sparkline_in_7d") val sparklineIn7d: SparklineDto?
)

data class SparklineDto(val price: List<Double>?)