package com.example.cmed_task.base

import com.example.cmed_task.utils.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): DataState<T> {
        return withContext(Dispatchers.IO) {
            try {
                DataState.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        DataState.Error(false, throwable.code(), throwable.response()?.message())
                    }
                    else -> {
                        DataState.Error(true, null, null)
                    }
                }
            }
        }
    }

}