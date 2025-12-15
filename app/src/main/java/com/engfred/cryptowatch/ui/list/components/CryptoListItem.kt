package com.engfred.cryptowatch.ui.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.engfred.cryptowatch.domain.model.CryptoCoin
import com.engfred.cryptowatch.ui.common.CoinImage
import com.engfred.cryptowatch.ui.common.SparklineChart

@Composable
fun CryptoListItem(coin: CryptoCoin, onCoinClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCoinClick(coin.id) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinImage(url = coin.image, contentDescription = coin.name)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(coin.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(coin.symbol, color = Color.Gray, fontSize = 14.sp)
        }
        val chartColor =
            if (coin.priceChangePercentage24h >= 0) Color(0xFF00C853) else Color(0xFFD50000)
        SparklineChart(
            coin.sparkline,
            Modifier
                .width(80.dp)
                .height(40.dp)
                .padding(horizontal = 8.dp),
            chartColor
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                "$${coin.currentPrice}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text("${coin.priceChangePercentage24h}%", color = chartColor, fontSize = 14.sp)
        }
    }
}