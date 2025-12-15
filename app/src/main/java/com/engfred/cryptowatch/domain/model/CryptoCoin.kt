package com.engfred.cryptowatch.domain.model

data class CryptoCoin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double,
    val priceChangePercentage24h: Double,
    val high24h: Double,
    val low24h: Double,
    val marketCap: Double,
    val volume: Double,
    val sparkline: List<Double>
)