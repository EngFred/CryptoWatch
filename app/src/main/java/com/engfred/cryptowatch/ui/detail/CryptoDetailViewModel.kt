package com.engfred.cryptowatch.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.engfred.cryptowatch.domain.model.CryptoCoin
import com.engfred.cryptowatch.domain.usecase.GetCoinDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CryptoDetailViewModel @Inject constructor(
    private val getCoinDetailUseCase: GetCoinDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val coinId: String = checkNotNull(savedStateHandle["coinId"])

    val uiState: StateFlow<CryptoDetailState> = getCoinDetailUseCase(coinId)
        .map<CryptoCoin, CryptoDetailState> { CryptoDetailState.Success(it) }
        .catch { emit(CryptoDetailState.Error(it.message ?: "Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CryptoDetailState.Loading
        )
}

sealed class CryptoDetailState {
    data object Loading : CryptoDetailState()
    data class Success(val coin: CryptoCoin) : CryptoDetailState()
    data class Error(val message: String) : CryptoDetailState()
}