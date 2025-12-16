package com.engfred.cryptowatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    val coinId: String,
    val prevKey: Int?,
    val nextKey: Int?,
    // Track when this key was created to manage cache expiration
    val lastUpdated: Long = System.currentTimeMillis()
)