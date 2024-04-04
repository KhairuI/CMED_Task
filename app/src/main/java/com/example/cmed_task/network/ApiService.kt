package com.example.cmed_task.network

import com.example.cmed_task.model.CharacterModel
import com.example.cmed_task.utils.AppConstants
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET(AppConstants.ENDPOINT_CHARACTER)
    suspend fun characterApiCall(): Response<CharacterModel>


}