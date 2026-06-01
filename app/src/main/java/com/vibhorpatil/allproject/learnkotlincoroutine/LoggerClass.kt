package com.vibhorpatil.allproject.learnkotlincoroutine

import android.util.Log
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggerClass @Inject constructor() {

    fun <T> logFlowData(data: List<T>) {
        data.filterIsInstance<ApiUser>()
            .forEach { user ->
                Log.d("TAG", "logFlowData: ${user.name + " " + user.avatar + " " + user.email}")
            }
    }
}