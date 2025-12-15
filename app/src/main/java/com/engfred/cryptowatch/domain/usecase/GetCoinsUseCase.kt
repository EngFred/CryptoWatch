package com.engfred.cryptowatch.domain.usecase

import androidx.paging.PagingData
import com.engfred.cryptowatch.domain.model.CryptoCoin
import com.engfred.cryptowatch.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoinsUseCase @Inject constructor(
    private val repository: CryptoRepository
) {
    operator fun invoke(query: String = ""): Flow<PagingData<CryptoCoin>> {
        return repository.getCoins(query)
    }
}