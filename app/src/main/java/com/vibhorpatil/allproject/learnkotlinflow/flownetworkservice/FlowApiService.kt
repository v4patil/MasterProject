package com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice

import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import retrofit2.http.GET

interface FlowApiService{
    @GET("users")
    suspend fun getUsers(): List<ApiUser>

    @GET("more-users")
    suspend fun getMoreUsers(): List<ApiUser>

    @GET("error")
    suspend fun getUsersWithError(): List<ApiUser>
}