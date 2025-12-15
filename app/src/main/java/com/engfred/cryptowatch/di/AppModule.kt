package com.engfred.cryptowatch.di

import android.app.Application
import androidx.room.Room
import com.engfred.cryptowatch.data.local.CryptoDatabase
import com.engfred.cryptowatch.data.remote.CoinGeckoApi
import com.engfred.cryptowatch.data.repository.CryptoRepositoryImpl
import com.engfred.cryptowatch.domain.repository.CryptoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApi(): CoinGeckoApi {
        return Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): CryptoDatabase {
        return Room.databaseBuilder(app, CryptoDatabase::class.java, "crypto_db")
            .fallbackToDestructiveMigration() // For development safety
            .build()
    }

    @Provides
    @Singleton
    fun provideRepository(api: CoinGeckoApi, db: CryptoDatabase): CryptoRepository {
        return CryptoRepositoryImpl(api, db)
    }
}