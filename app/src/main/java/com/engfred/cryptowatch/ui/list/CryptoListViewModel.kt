package com.engfred.cryptowatch.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.engfred.cryptowatch.domain.model.CryptoCoin
import com.engfred.cryptowatch.domain.usecase.GetCoinsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class CryptoListViewModel @Inject constructor(
    private val getCoinsUseCase: GetCoinsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val coinsFlow: Flow<PagingData<CryptoCoin>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query -> getCoinsUseCase(query) }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}