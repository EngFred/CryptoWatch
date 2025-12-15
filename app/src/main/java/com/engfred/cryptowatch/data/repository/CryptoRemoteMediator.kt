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
import retrofit2.HttpException
import java.io.IOException

private const val TAG = "CryptoDebug"

@OptIn(ExperimentalPagingApi::class)
class CryptoRemoteMediator(
    private val query: String,
    private val api: CoinGeckoApi,
    private val db: CryptoDatabase
) : RemoteMediator<Int, CryptoEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CryptoEntity>
    ): MediatorResult {
        return try {
            Log.d(TAG, "Mediator: load() called with LoadType: $loadType, Query: '$query'")

            val page = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d(TAG, "Mediator: Refreshing data (Page 1)")
                    1
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey

                    if (nextKey == null) {
                        Log.d(TAG, "Mediator: Append blocked (nextKey is null). End of pagination.")
                        return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    }

                    Log.d(TAG, "Mediator: Appending data. Next Page: $nextKey")
                    nextKey
                }
            }

            val apiResponse = if (query.isEmpty()) {
                Log.d(TAG, "Mediator: Fetching standard list from API (Page $page)")
                val response = api.getCoins(page = page, perPage = state.config.pageSize)
                response.map { it.toEntity(page) }
            } else {
                if (loadType == LoadType.REFRESH) {
                    Log.d(TAG, "Mediator: Searching Global API for '$query'")
                    val searchResult = api.searchGlobal(query)
                    val topIds = searchResult.coins.take(10).joinToString(",") { it.id }

                    if (topIds.isNotEmpty()) {
                        Log.d(TAG, "Mediator: Found IDs [$topIds], fetching details...")
                        val details = api.getCoins(ids = topIds)
                        details.map { it.toEntity(1) }
                    } else {
                        Log.d(TAG, "Mediator: Search returned no results.")
                        emptyList()
                    }
                } else {
                    Log.d(TAG, "Mediator: Skipping pagination for search query.")
                    emptyList()
                }
            }

            Log.d(TAG, "Mediator: API Success. Received ${apiResponse.size} items.")

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    if (query.isEmpty()) {
                        Log.d(TAG, "Mediator: Clearing local database (Full Refresh)")
                        db.dao().clearRemoteKeys()
                        db.dao().clearCoins()
                    } else {
                        Log.d(TAG, "Mediator: Refreshing search results (Keeping non-conflicting data)")
                    }
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (apiResponse.isEmpty()) null else page + 1

                val keys = apiResponse.map {
                    RemoteKeys(coinId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                Log.d(TAG, "Mediator: Saving ${keys.size} keys and items to Room DB")
                db.dao().insertAllRemoteKeys(keys)
                db.dao().insertAll(apiResponse)
            }

            MediatorResult.Success(endOfPaginationReached = apiResponse.isEmpty())
        } catch (e: IOException) {
            Log.e(TAG, "Mediator Error (Network): ${e.localizedMessage}")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "Mediator Error (HTTP): ${e.localizedMessage}")
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CryptoEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { coin ->
            db.dao().getRemoteKeysForCoinId(coin.id)
        }
    }
}