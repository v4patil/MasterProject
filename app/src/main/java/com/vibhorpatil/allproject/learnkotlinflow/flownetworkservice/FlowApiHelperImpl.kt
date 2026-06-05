package com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice

import kotlinx.coroutines.flow.flow

class FlowApiHelperImpl(private val apiService : FlowApiService) : FlowApiHelper {
    override fun getUsers() = flow { emit(apiService.getUsers()) }

    override fun getMoreUsers() = flow { emit(apiService.getMoreUsers()) }

    override fun getUsersWithError() = flow { emit(apiService.getUsersWithError()) }
}