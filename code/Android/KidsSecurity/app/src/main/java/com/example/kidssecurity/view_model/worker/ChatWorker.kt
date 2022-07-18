package com.example.kidssecurity.view_model.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class ChatWorker(
    context: Context,
    workerParameters: WorkerParameters
): CoroutineWorker(context,workerParameters) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }
}