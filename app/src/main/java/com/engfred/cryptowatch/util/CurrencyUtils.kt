package com.engfred.cryptowatch.util

object CurrencyUtils {
    fun formatLargeNumber(value: Double): String {
        return when {
            value >= 1_000_000_000 -> "$${String.format("%.2f", value / 1_000_000_000)}B"
            value >= 1_000_000 -> "$${String.format("%.2f", value / 1_000_000)}M"
            else -> "$${String.format("%.0f", value)}"
        }
    }
}