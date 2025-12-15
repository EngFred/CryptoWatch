package com.engfred.cryptowatch.domain.usecase

import com.engfred.cryptowatch.domain.repository.CryptoRepository
import javax.inject.Inject

class GetCoinDetailUseCase @Inject constructor(
    private val repository: CryptoRepository
) {
    operator fun invoke(id: String) = repository.getCoin(id)
}