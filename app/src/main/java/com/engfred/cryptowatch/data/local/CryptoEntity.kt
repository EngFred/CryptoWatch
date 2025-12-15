package com.engfred.cryptowatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coins")
data class CryptoEntity(
    @PrimaryKey val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    val currentPrice: Double,
    val priceChangePercentage24h: Double,
    val high24h: Double,
    val low24h: Double,
    val marketCap: Double,
    val volume: Double,
    val sparkline: List<Double>,
    val page: Int // Tracks which API page this coin belongs to for pagination order
)