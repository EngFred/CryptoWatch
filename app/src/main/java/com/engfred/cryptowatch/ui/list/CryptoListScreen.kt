package com.engfred.cryptowatch.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.engfred.cryptowatch.ui.list.components.CryptoListItem
import com.engfred.cryptowatch.ui.list.components.ErrorItem
import com.engfred.cryptowatch.ui.list.components.OfflineBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoListScreen(
    viewModel: CryptoListViewModel = hiltViewModel(),
    onCoinClick: (String) -> Unit
) {
    val coins = viewModel.coinsFlow.collectAsLazyPagingItems()
    val query by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .systemBarsPadding()
    ) {

        TextField(
            value = query,
            onValueChange = viewModel::onSearchQueryChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Search coins...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        val refreshState = coins.loadState.refresh

        val isRefreshing = refreshState is LoadState.Loading && coins.itemCount > 0

        val isInitialLoading = refreshState is LoadState.Loading && coins.itemCount == 0

        if (refreshState !is LoadState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isInitialLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            val pullRefreshState = rememberPullToRefreshState()

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { coins.refresh() },
                modifier = Modifier.fillMaxSize(),
                state = pullRefreshState,
                contentAlignment = Alignment.TopCenter,
                indicator = {
                    PullToRefreshDefaults.Indicator(
                        state = pullRefreshState,
                        isRefreshing = isRefreshing,
                        containerColor = Color(0xFF1E1E1E),
                        color = Color.White,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    if (refreshState is LoadState.Error) {
                        item {
                            OfflineBanner(
                                message = "Failed to refresh data. No internet connection!"
                            )
                        }
                    }

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

                    when (coins.loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                }
                            }
                        }
                        is LoadState.Error -> {
                            item {
                                ErrorItem(
                                    errorMessage = "Failed to load more items.",
                                    onRetryClick = { coins.retry() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}