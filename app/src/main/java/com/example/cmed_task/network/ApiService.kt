package com.example.cmed_task.network

import com.example.cmed_task.model.CharacterModel
import com.example.cmed_task.utils.AppConstants
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface ApiService {

    @GET(AppConstants.ENDPOINT_CHARACTER)
    suspend fun characterApiCall(): Response<CharacterModel>

    @Streaming
    @GET(AppConstants.ENDPOINT_VIDEO)
    suspend fun videoApiCall(): ResponseBody

}