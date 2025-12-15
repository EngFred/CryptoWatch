package com.engfred.cryptowatch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [CryptoEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun dao(): CryptoDao
}