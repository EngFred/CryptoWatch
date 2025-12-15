package com.engfred.cryptowatch.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.engfred.cryptowatch.ui.common.CoinImage
import com.engfred.cryptowatch.ui.common.SparklineChart

@Composable
fun CryptoDetailScreen(viewModel: CryptoDetailViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        when (val s = state) {
            is CryptoDetailState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            is CryptoDetailState.Error -> Text(s.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            is CryptoDetailState.Success -> {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CoinImage(s.coin.image, s.coin.name, Modifier.size(64.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(s.coin.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(s.coin.symbol, color = Color.Gray, fontSize = 18.sp)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("$${s.coin.currentPrice}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(24.dp))
                    SparklineChart(s.coin.sparkline, Modifier.fillMaxWidth().height(200.dp))
                    // Add grid stats here as per design
                }
            }
        }
    }
}