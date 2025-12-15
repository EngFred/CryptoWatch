package com.engfred.cryptowatch.data.repository

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

class CryptoRepositoryImpl @Inject constructor(
    private val api: CoinGeckoApi,
    private val db: CryptoDatabase
) : CryptoRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCoins(query: String): Flow<PagingData<CryptoCoin>> {
        val pagingSourceFactory = {
            if (query.isEmpty()) db.dao().getCoinsPagingSource()
            else db.dao().searchCoinsPagingSource(query)
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
        return db.dao().getCoin(id).map { it?.toDomain() ?: throw Exception("Coin not found") }
    }

    override suspend fun triggerSync(): Result<Unit> {
        return try {
            // Worker Sync: Fetch page 1 to update top movers
            val coins = api.getCoins(page = 1)
            db.dao().insertAll(coins.map { it.toEntity(1) })
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}