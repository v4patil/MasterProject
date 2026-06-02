package com.vibhorpatil.allproject.learnkotlincoroutine

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibhorpatil.allproject.dependencyinjection.di.ActivityScope
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiHelper
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
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

    //    withTimeout()
//    Cancels the coroutine if the timeout is reached.
//    Throws a TimeoutCancellationException.
    fun doWithTimeOut() {
        viewModelScope.launch {
            try {
                withTimeout(1) {
                    val users = apiHelper.getUsers()
                    loggerClass.logFlowData(users)
                }
            } catch (e: TimeoutCancellationException) {
                loggerClass.logFlowData(listOf(1, 2, 3))
            }

        }
    }

    //    withTimeoutOrNull()
//    Cancels the coroutine if the timeout is reached.
//    Does not throw an exception.
//    Returns null instead.
    fun doWithTimeOutOrNull() {
        viewModelScope.launch {
            val result = withTimeoutOrNull(1) {
                apiHelper.getUsers()
            } ?: listOf(1, 2, 3)
            loggerClass.logFlowData(result)
        }
    }

    fun doWithTryCatch() {
        viewModelScope.launch {
            try {
                val usersFromApi = apiHelper.getUsersWithError()
                loggerClass.logFlowData(usersFromApi)
            } catch (e: Exception) {
                loggerClass.logFlowData(listOf(1, 2, 3))
            }
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        Log.d("TAG", "Exception caught")
    }

    fun doWithExceptionHandler() {
        viewModelScope.launch(exceptionHandler + CoroutineName("exceptionHandler")) {
            Log.d(
                "TAG",
                "Coroutine Name: ${coroutineContext[CoroutineName]?.name}"
            ) // output --> exceptionHandler
            Log.d(
                "TAG",
                "Dispatcher: ${coroutineContext[CoroutineDispatcher]}"
            ) // output --> Dispatchers.Main.immediate
            val usersFromApi = apiHelper.getUsersWithError()
            loggerClass.logFlowData(usersFromApi)
        }
    }

    fun doIgnoreErrorAndContinue() {
        viewModelScope.launch(exceptionHandler + CoroutineName("exceptionHandler")) {
// supervisorScope is needed, so that we can ignore error and continue
// here, more than two child jobs are running in parallel under a supervisor, one child job gets failed, we can continue with other.
            supervisorScope {
                val users = async { apiHelper.getUsers() }
                val usersWithError = async { apiHelper.getUsersWithError() }

                val list = mutableListOf<ApiUser>()

                try {
                    list.addAll(users.await())
                } catch (e: Exception) {
                    Log.e("TAG", "users failed", e)
                }

                try {
                    list.addAll(usersWithError.await())
                } catch (e: Exception) {
                    Log.e("TAG", "usersWithError failed", e)
                }

                loggerClass.logFlowData(list)
            }
        }
    }

    private suspend fun doLongRunningTask() {
        withContext(Dispatchers.Default) {
            // your code for doing a long running task
            // Added delay to simulate
            delay(5000)
        }
    }

    fun doSingleLongRunningTask() {
        viewModelScope.launch {
            doLongRunningTask()
            loggerClass.logFlowData(listOf(1))
        }
    }

    private suspend fun doLongRunningTaskOne(): Int {
        return withContext(Dispatchers.Default) {
            // your code for doing a long running task
            // Added delay to simulate
            delay(2000)
            return@withContext 10
        }
    }

    private suspend fun doLongRunningTaskTwo(): Int {
        return withContext(Dispatchers.Default) {
            // your code for doing a long running task
            // Added delay to simulate
            delay(2000)
            return@withContext 10
        }
    }

    fun doTwoLongRunningTask() {
        viewModelScope.launch {
            val resultOneDeferred = async { doLongRunningTaskOne() }
            val resultTwoDeferred = async { doLongRunningTaskTwo() }
            val combinedResult = resultOneDeferred.await() + resultTwoDeferred.await()

            Log.d("TAG", "doTwoLongRunningTask: $combinedResult")

        }
    }


}
