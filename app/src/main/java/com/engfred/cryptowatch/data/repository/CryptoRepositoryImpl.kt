package com.engfred.cryptowatch.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.engfred.cryptowatch.data.local.CryptoDatabase
import com.engfred.cryptowatch.data.mapper.toDomain
import com.engfred.cryptowatch.data.mapper.toEntity
import com.engfred.cryptowatch.data.remote.CoinGeckoApi
import com.engfred.cryptowatch.domain.model.CryptoCoin
import com.engfred.cryptowatch.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "CryptoDebug"

class CryptoRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi,
    private val db: CryptoDatabase
) : CryptoRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCoins(query: String): Flow<PagingData<CryptoCoin>> {
        Log.d(TAG, "Repo: Creating new Pager. Query: '$query'")

        val pagingSourceFactory = {
            if (query.isEmpty()) {
                Log.d(TAG, "Repo: Using Standard Paging Source")
                db.dao().getCoinsPagingSource()
            } else {
                Log.d(TAG, "Repo: Using Search Paging Source for '$query'")
                db.dao().searchCoinsPagingSource(query)
            }
        }

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = CryptoRemoteMediator(query, api, db),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun getCoin(id: String): Flow<CryptoCoin> {
        Log.d(TAG, "Repo: Observing coin details for ID: $id")
        return db.dao().getCoin(id).map {
            it?.toDomain() ?: throw Exception("Coin not found")
        }
    }

    override suspend fun triggerSync(): Result<Unit> {
        Log.d(TAG, "Repo: Background sync started...")
        return try {
            val coins = api.getCoins(page = 1)
            Log.d(TAG, "Repo: Sync fetched ${coins.size} items. Saving to DB.")
            db.dao().insertAll(coins.map { it.toEntity(1) })
            Log.d(TAG, "Repo: Sync complete.")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Repo: Sync failed: ${e.message}")
            Result.failure(e)
        }
    }
}