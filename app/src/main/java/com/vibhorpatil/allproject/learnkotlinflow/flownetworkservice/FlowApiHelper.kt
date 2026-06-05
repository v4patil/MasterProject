package com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice

import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import kotlinx.coroutines.flow.Flow

interface FlowApiHelper {

    fun getUsers(): Flow<List<ApiUser>>

    fun getMoreUsers(): Flow<List<ApiUser>>

    fun getUsersWithError(): Flow<List<ApiUser>>
}