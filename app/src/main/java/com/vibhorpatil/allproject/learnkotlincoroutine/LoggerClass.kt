package com.vibhorpatil.allproject.learnkotlincoroutine

import android.util.Log
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggerClass @Inject constructor() {

    fun <T> logFlowData(data: List<T>) {
        data.filterIsInstance<ApiUser>()
            .forEach { user ->
                Log.d("TAG", "logFlowData: ${user.name + " " + user.avatar + " " + user.email}")
            }
        data.filterIsInstance<User>()
            .forEach { user ->
                Log.d("TAG", "${user.name + " " + user.avatar}")
            }
        data.filterIsInstance<Int>()
            .forEach {
                Log.d("TAG", it.toString())
            }
    }

    fun logMessage(msg: String){
        Log.d("TAG", msg)
    }
}