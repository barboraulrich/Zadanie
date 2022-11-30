package com.example.zadanie.helpers

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.zadanie.data.DataRepository
import com.example.zadanie.data.api.RestApi
import com.example.zadanie.data.db.AppRoomDatabase
import com.example.zadanie.data.db.LocalCache

object Injection {
    private fun provideCache(context: Context): LocalCache {
        val database = AppRoomDatabase.getInstance(context)
        return LocalCache(database.appDao())
    }

    fun provideDataRepository(context: Context): DataRepository {
        return DataRepository.getInstance(RestApi.create(context), provideCache(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(
            provideDataRepository(
                context
            )
        )
    }
}