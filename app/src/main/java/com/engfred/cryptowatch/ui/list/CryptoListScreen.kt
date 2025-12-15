package com.engfred.cryptowatch.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.engfred.cryptowatch.domain.model.CryptoCoin
import com.engfred.cryptowatch.ui.common.CoinImage
import com.engfred.cryptowatch.ui.common.SparklineChart

@Composable
fun CryptoListScreen(
    viewModel: CryptoListViewModel = hiltViewModel(),
    onCoinClick: (String) -> Unit
) {
    val coins = viewModel.coinsFlow.collectAsLazyPagingItems()
    val query by viewModel.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        TextField(
            value = query,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Search coins...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                count = coins.itemCount,
                key = coins.itemKey { it.id },
                contentType = coins.itemContentType { "coin" }
            ) { index ->
                val coin = coins[index]
                if (coin != null) {
                    CryptoListItem(coin, onCoinClick)
                    HorizontalDivider(color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun CryptoListItem(coin: CryptoCoin, onCoinClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCoinClick(coin.id) }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinImage(url = coin.image, contentDescription = coin.name)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(coin.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(coin.symbol, color = Color.Gray, fontSize = 14.sp)
        }
        val chartColor = if (coin.priceChangePercentage24h >= 0) Color(0xFF00C853) else Color(0xFFD50000)
        SparklineChart(coin.sparkline, Modifier.width(80.dp).height(40.dp).padding(horizontal = 8.dp), chartColor)
        Column(horizontalAlignment = Alignment.End) {
            Text("$${coin.currentPrice}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("${coin.priceChangePercentage24h}%", color = chartColor, fontSize = 14.sp)
        }
    }
}