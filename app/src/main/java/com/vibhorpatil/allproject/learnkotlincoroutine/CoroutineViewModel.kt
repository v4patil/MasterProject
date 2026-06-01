package com.vibhorpatil.allproject.learnkotlincoroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibhorpatil.allproject.dependencyinjection.di.ActivityScope
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiHelper
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScope
class CoroutineViewModel @Inject constructor(
    val apiHelper: ApiHelper,
    val loggerClass: LoggerClass
) : ViewModel() {


    fun doSingleNetworkCall() {
        viewModelScope.launch {
            val userFromAPI = apiHelper.getUsers()
            loggerClass.logFlowData(userFromAPI)
        }
    }

    fun doSeriesNetworkCall() {
        viewModelScope.launch {
            val userFromAPI = apiHelper.getUsers()
            val moreUserFromAPI = apiHelper.getMoreUsers()

            val allUsersFromAPi = mutableListOf<ApiUser>()
            allUsersFromAPi.addAll(userFromAPI)
            allUsersFromAPi.addAll(moreUserFromAPI)

            loggerClass.logFlowData(allUsersFromAPi)
        }
    }

// Now job1 is not a child of the current launch; it's a sibling coroutine under viewModelScope.
// This can lead to different cancellation and exception behavior.
//    fun doParallelNetworkCall() {
//        viewModelScope.launch {
//            val job1 = viewModelScope.async { apiHelper.getUsers() }
//            val job2 = async { apiHelper.getMoreUsers() }
//        }
//    }

    fun doParallelNetworkCall() {
        viewModelScope.launch {
            val job1 = async { apiHelper.getUsers() }
            val job2 = async { apiHelper.getMoreUsers() }

            val users = job1.await()
            val moreUsers = job2.await()

            val result = users + moreUsers

            loggerClass.logFlowData(result)
        }
    }


}
