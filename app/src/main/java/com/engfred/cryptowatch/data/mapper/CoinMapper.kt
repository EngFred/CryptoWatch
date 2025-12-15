package com.engfred.cryptowatch.data.mapper

import com.engfred.cryptowatch.data.local.CryptoEntity
import com.engfred.cryptowatch.data.remote.CoinDto
import com.engfred.cryptowatch.domain.model.CryptoCoin

fun CoinDto.toEntity(page: Int) = CryptoEntity(
    id = id,
    symbol = symbol.uppercase(),
    name = name,
    image = image,
    currentPrice = currentPrice,
    priceChangePercentage24h = priceChangePercentage24h,
    high24h = high24h,
    low24h = low24h,
    marketCap = marketCap,
    volume = totalVolume,
    sparkline = sparklineIn7d?.price ?: emptyList(),
    page = page
)

fun CryptoEntity.toDomain() = CryptoCoin(
    id = id,
    symbol = symbol,
    name = name,
    image = image,
    currentPrice = currentPrice,
    priceChangePercentage24h = priceChangePercentage24h,
    high24h = high24h,
    low24h = low24h,
    marketCap = marketCap,
    volume = volume,
    sparkline = sparkline
)