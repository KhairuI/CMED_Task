package com.example.cmed_task.repository

import com.example.cmed_task.base.BaseRepository
import com.example.cmed_task.network.ApiService

class Task2Repository(private val api: ApiService) : BaseRepository() {


    // get all character list
    suspend fun getCharacterList() = safeApiCall {
        api.characterApiCall()
    }

    // get all character list
    suspend fun getVideo() = safeApiCall {
        api.videoApiCall()
    }



}