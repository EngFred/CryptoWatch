package com.engfred.cryptowatch.domain.repository

import androidx.paging.PagingData
import com.engfred.cryptowatch.domain.model.CryptoCoin
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {

    fun getCoins(query: String): Flow<PagingData<CryptoCoin>>

    fun getCoin(id: String): Flow<CryptoCoin>

    suspend fun triggerSync(): Result<Unit>
}