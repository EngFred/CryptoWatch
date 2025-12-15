package com.engfred.cryptowatch.data.repository

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
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            // HYBRID LOGIC:
            // If query is empty -> Standard Pagination
            // If query exists -> Global Search API -> Fetch Details -> Save
            val apiResponse = if (query.isEmpty()) {
                val response = api.getCoins(page = page, perPage = state.config.pageSize)
                response.map { it.toEntity(page) }
            } else {
                // Search Logic: Only run on first load (REFRESH)
                if (loadType == LoadType.REFRESH) {
                    val searchResult = api.searchGlobal(query)
                    val topIds = searchResult.coins.take(10).joinToString(",") { it.id }
                    if (topIds.isNotEmpty()) {
                        val details = api.getCoins(ids = topIds)
                        details.map { it.toEntity(1) }
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList() // No pagination for search results in this implementation
                }
            }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    if (query.isEmpty()) {
                        // Only clear DB if we are doing a full refresh of the main list
                        // For search, we might want to keep existing data, but for this strict logic,
                        // we treat the DB as a cache that reflects current view.
                        // However, to support offline, we should be careful.
                        // Ideally, we keep old data but overwrite collisions.
                        // But Paging 3 needs a consistent state.
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
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CryptoEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { coin ->
            db.dao().getRemoteKeysForCoinId(coin.id)
        }
    }
}