package com.engfred.cryptowatch.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.engfred.cryptowatch.ui.common.SparklineChart
import com.engfred.cryptowatch.ui.detail.components.StatCard
import com.engfred.cryptowatch.ui.detail.components.TimeTab
import com.engfred.cryptowatch.util.CurrencyUtils.formatLargeNumber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoDetailScreen(
    viewModel: CryptoDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            if (state is CryptoDetailState.Success) {
                val coin = (state as CryptoDetailState.Success).coin
                TopAppBar(
                    title = { Text(coin.name, color = Color.White, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
                )
            }
        },
        bottomBar = {
            // sticky Trade button
            Button(
                onClick = { /* TODO: Trade Action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)), // Coinbase Blue
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Trade", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        },
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val s = state) {
                is CryptoDetailState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF2979FF)
                )

                is CryptoDetailState.Error -> Text(
                    text = s.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )

                is CryptoDetailState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp)
                    ) {
                        // 1. Header Price Section
                        Text(
                            text = "$${s.coin.currentPrice}",
                            color = Color.White,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isPositive = s.coin.priceChangePercentage24h >= 0
                            val color = if (isPositive) Color(0xFF00C853) else Color(0xFFD50000)
                            Text(
                                text = "${if (isPositive) "+" else ""}${s.coin.priceChangePercentage24h}%",
                                color = color,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "24h",
                                color = Color.Gray,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 2. Chart Section (Card Look)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Chart
                                val chartColor = if (s.coin.priceChangePercentage24h >= 0) Color(0xFF00C853) else Color(0xFFD50000)
                                SparklineChart(
                                    data = s.coin.sparkline,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp),
                                    graphColor = chartColor
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Time Range Tabs (Visual Only)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    TimeTab("1H", false)
                                    TimeTab("1D", true)
                                    TimeTab("1W", false)
                                    TimeTab("1Y", false)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Stats Grid (2x2)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatCard(
                                title = "Market Cap",
                                value = formatLargeNumber(s.coin.marketCap),
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "High 24h",
                                value = "$${s.coin.high24h}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            StatCard(
                                title = "Low 24h",
                                value = "$${s.coin.low24h}",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Volume",
                                value = formatLargeNumber(s.coin.volume),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}