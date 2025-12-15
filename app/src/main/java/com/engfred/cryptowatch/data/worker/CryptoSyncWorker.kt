package com.engfred.cryptowatch.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.engfred.cryptowatch.domain.repository.CryptoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "CryptoDebug"

@HiltWorker
class CryptoSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CryptoRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Worker: CryptoSyncWorker started executing.")

        val result = repository.triggerSync()

        return if (result.isSuccess) {
            Log.d(TAG, "Worker: Sync Successful.")
            Result.success()
        } else {
            Log.w(TAG, "Worker: Sync Failed. Retrying...")
            Result.retry()
        }
    }
}