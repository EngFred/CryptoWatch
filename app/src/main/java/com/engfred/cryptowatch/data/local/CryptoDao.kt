package com.engfred.cryptowatch.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coins: List<CryptoEntity>)

    @Query("SELECT * FROM coins ORDER BY page ASC, marketCap DESC")
    fun getCoinsPagingSource(): PagingSource<Int, CryptoEntity>

    @Query("""
        SELECT * FROM coins 
        WHERE name LIKE '%' || :query || '%' 
        OR symbol LIKE '%' || :query || '%' 
        ORDER BY marketCap DESC
    """)
    fun searchCoinsPagingSource(query: String): PagingSource<Int, CryptoEntity>

    @Query("SELECT * FROM coins WHERE id = :id")
    fun getCoin(id: String): Flow<CryptoEntity>

    @Query("DELETE FROM coins")
    suspend fun clearCoins()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKeys: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE coinId = :coinId")
    suspend fun getRemoteKeysForCoinId(coinId: String): RemoteKeys?

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}