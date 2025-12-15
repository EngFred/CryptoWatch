package com.engfred.cryptowatch.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.engfred.cryptowatch.domain.repository.CryptoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CryptoSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CryptoRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val result = repository.triggerSync()
        return if (result.isSuccess) Result.success() else Result.retry()
    }
}