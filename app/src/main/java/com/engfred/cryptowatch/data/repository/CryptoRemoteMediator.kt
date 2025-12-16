package com.engfred.cryptowatch.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.engfred.cryptowatch.data.local.CryptoDatabase
import com.engfred.cryptowatch.data.local.CryptoEntity
import com.engfred.cryptowatch.data.local.RemoteKeys
import com.engfred.cryptowatch.data.remote.CoinGeckoApi
import com.engfred.cryptowatch.data.mapper.toEntity
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

private const val TAG = "CryptoDebug"

@OptIn(ExperimentalPagingApi::class)
class CryptoRemoteMediator(
    private val query: String,
    private val api: CoinGeckoApi,
    private val db: CryptoDatabase
) : RemoteMediator<Int, CryptoEntity>() {

    override suspend fun initialize(): InitializeAction {
        // If the data is fresh (i.e < 5 minutes old), we skip the initial network call.
        // If it's stale, we trigger a refresh.
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
        val lastUpdated = db.dao().getCreationTime() ?: 0L
        val now = System.currentTimeMillis()

        return if (now - lastUpdated <= cacheTimeout) {
            Log.d(TAG, "Mediator: Data is fresh (Last updated: ${(now - lastUpdated) / 1000}s ago). Skipping Initial Refresh.")
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            Log.d(TAG, "Mediator: Data is stale or missing. Launching Initial Refresh.")
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CryptoEntity>
    ): MediatorResult {
        return try {
            // Forcing a small pause to prevent rate limiting, but only on Appends
            if (loadType != LoadType.REFRESH) {
                delay(1000)
            }

            Log.d(TAG, "Mediator: load() called with LoadType: $loadType")

            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    if (nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }
                    nextKey
                }
            }

            // Logic to fetch from API
            val apiResponse = if (query.isEmpty()) {
                val response = api.getCoins(page = page, perPage = state.config.pageSize)
                response.map { it.toEntity(page) }
            } else {
                if (loadType == LoadType.REFRESH) {
                    val searchResult = api.searchGlobal(query)
                    val topIds = searchResult.coins.take(10).joinToString(",") { it.id }
                    if (topIds.isNotEmpty()) {
                        val details = api.getCoins(ids = topIds)
                        details.map { it.toEntity(1) }
                    } else emptyList()
                } else emptyList()
            }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    if (query.isEmpty()) {
                        db.dao().clearRemoteKeys()
                        db.dao().clearCoins()
                    }
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (apiResponse.isEmpty()) null else page + 1

                val keys = apiResponse.map {
                    RemoteKeys(coinId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                db.dao().insertAllRemoteKeys(keys)
                db.dao().insertAll(apiResponse)
            }

            MediatorResult.Success(endOfPaginationReached = apiResponse.isEmpty())
        } catch (e: IOException) {
            Log.e(TAG, "Mediator Error (Network): ${e.localizedMessage}")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "Mediator Error (HTTP): ${e.code()}")
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CryptoEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { coin ->
            db.dao().getRemoteKeysForCoinId(coin.id)
        }
    }
}