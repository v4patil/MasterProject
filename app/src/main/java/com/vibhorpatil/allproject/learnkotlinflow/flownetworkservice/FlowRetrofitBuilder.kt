package com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FlowRetrofitBuilder {

    private const val BASE_URL = "https://5e510330f2c0d300147c034c.mockapi.io/"


    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val flowApiService: FlowApiService = getRetrofit().create(FlowApiService::class.java)

}