package com.vibhorpatil.allproject.learnkotlincoroutine

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibhorpatil.allproject.dependencyinjection.di.ActivityScope
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiHelper
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.Executors
import javax.inject.Inject

@ActivityScope
class CoroutineViewModel @Inject constructor(
    val apiHelper: ApiHelper,
    val loggerClass: LoggerClass
) : ViewModel() {
/**
 * The default dispatcher for launch {} depends on the CoroutineScope you're using.
 * 1. viewModelScope.launch {} -> Default dispatcher = Dispatchers.Main.immediate (effectively Main/UI thread).
 * 2. lifecycleScope.launch {}  -> Default dispatcher = Dispatchers.Main.
 * 3. GlobalScope.launch {} -> Default dispatcher = Dispatchers.Default.
 * 4. Custom CoroutineScope -> The launched coroutine inherits th
 **/
    val TAG = "TAG"

    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        loggerClass.logMessage("Exception caught: ${e.message}")
    }

    private val myActivityScope = CoroutineScope(Dispatchers.Main.immediate)

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

    fun doWithTimeOut() {
        viewModelScope.launch {
            try {
                withTimeout(1) {
                    val users = apiHelper.getUsers()
                    loggerClass.logFlowData(users)
                }
            } catch (e: TimeoutCancellationException) {
                loggerClass.logFlowData(emptyList<ApiUser>())
            }
        }
    }

    fun doWithTimeOutOrNull() {
        viewModelScope.launch {
            val result = withTimeoutOrNull(1) {
                apiHelper.getUsers()
            } ?: emptyList<ApiUser>()
            loggerClass.logFlowData(result)
        }
    }

    fun doWithTryCatch() {
        viewModelScope.launch {
            try {
                val usersFromApi = apiHelper.getUsersWithError()
                loggerClass.logFlowData(usersFromApi)
            } catch (e: Exception) {
                loggerClass.logFlowData(emptyList<ApiUser>())
            }
        }
    }

    fun doWithExceptionHandler() {
        viewModelScope.launch(exceptionHandler + CoroutineName("exceptionHandler")) {
            val usersFromApi = apiHelper.getUsersWithError()
            loggerClass.logFlowData(usersFromApi)
        }
    }

    fun doIgnoreErrorAndContinue() {
        viewModelScope.launch(exceptionHandler) {
            supervisorScope {
                val users = async { apiHelper.getUsers() }
                val usersWithError = async { apiHelper.getUsersWithError() }

                val list = mutableListOf<ApiUser>()
                try {
                    list.addAll(users.await())
                } catch (e: Exception) {
                    loggerClass.logMessage("users failed: ${e.message}")
                }

                try {
                    list.addAll(usersWithError.await())
                } catch (e: Exception) {
                    loggerClass.logMessage("usersWithError failed: ${e.message}")
                }
                loggerClass.logFlowData(list)
            }
        }
    }

    private suspend fun doLongRunningTask() {
        withContext(Dispatchers.Default) {
            loggerClass.logMessage("Before Delay")
            delay(2000)
            loggerClass.logMessage("After Delay")
        }
    }

    fun doSingleLongRunningTask() {
        viewModelScope.launch {
            doLongRunningTask()
            loggerClass.logFlowData(emptyList<ApiUser>())
        }
    }

    suspend fun doLongRunningTaskOne(): Int {
        return withContext(Dispatchers.Default) {
            delay(2000)
            return@withContext 10
        }
    }

    suspend fun doLongRunningTaskTwo(): Int {
        return withContext(Dispatchers.Default) {
            delay(2000)
            return@withContext 10
        }
    }

    fun doTwoLongRunningTask() {
        viewModelScope.launch {
            val resultOneDeferred = async { doLongRunningTaskOne() }
            val resultTwoDeferred = async { doLongRunningTaskTwo() }
            val combinedResult = resultOneDeferred.await() + resultTwoDeferred.await()
            loggerClass.logMessage("doTwoLongRunningTask: $combinedResult")
        }
    }

    /** Coroutine Basics **/

    fun testCoroutine() {
        loggerClass.logMessage("Function Start")
        viewModelScope.launch {//Default -> Dispatchers.Main.immediate
            loggerClass.logMessage("Function Task Start")
            doLongRunningTask()
            loggerClass.logMessage("Function Task End")
        }
        loggerClass.logMessage("Function End")
    }
    /*
    Function Start
    Function Task Start
    Function End
    Before Delay
    After Delay
    Function Task End
    */

    fun testCoroutineWithMain() {
        loggerClass.logMessage("Function Start")
        viewModelScope.launch(Dispatchers.Main) {
            loggerClass.logMessage("Function Task Start")
            doLongRunningTask()
            loggerClass.logMessage("Function Task End")
        }
        loggerClass.logMessage("Function End")
    }
    /*
    Function Start
    Function End
    Function Task Start
    Before Delay
    After Delay
    Function Task End
    */

    fun testCoroutineWithMainImmediate() {
        loggerClass.logMessage("Function Start")
        viewModelScope.launch(Dispatchers.Main.immediate) {
            loggerClass.logMessage("Function Task Start")
            doLongRunningTask()
            loggerClass.logMessage("Function Task End")
        }
        loggerClass.logMessage("Function End")
    }
    /*
    Function Start
    Function Task Start
    Function End
    Before Delay
    After Delay
    Function Task End
    */

    fun testCoroutineEverything() {
        loggerClass.logMessage("Function Start")

        viewModelScope.launch(Dispatchers.Main) {
            loggerClass.logMessage("Before Task 1")
            doLongRunningTask()
            loggerClass.logMessage("After Task 1")
        }

        viewModelScope.launch(Dispatchers.Main) {
            loggerClass.logMessage("Before Task 2")
            doLongRunningTask()
            loggerClass.logMessage("After Task 2")
        }

        loggerClass.logMessage("Function End")
    }
    /*
    Function Start
    Function End
    Before Task 1
    Before Task 2
    Before Delay
    Before Delay
    After Delay
    After Task 1
    After Delay
    After Task 2
    */

    fun usingGlobalScope() {//default value = Dispatcher.Default
        loggerClass.logMessage("Function Start")
        GlobalScope.launch {
            loggerClass.logMessage("Before Task")
            doLongRunningTask()
            loggerClass.logMessage("After Task")
        }
        loggerClass.logMessage("Function End")
    }
    /*
    Function Start
    Function End
    Before Task
    Before Delay
    After Delay
    After Task
    */

    fun globalScopeInsideLifecycleScope() {
        loggerClass.logMessage("Function Start")
        viewModelScope.launch {
            loggerClass.logMessage("Before Task")
            GlobalScope.launch(Dispatchers.Default) {
                loggerClass.logMessage("Before Delay")
                delay(2000)
                loggerClass.logMessage("After Delay")
            }
            loggerClass.logMessage("After Task")
        }
        loggerClass.logMessage("Function End")
    }

    fun usingMyActivityScope() {
        loggerClass.logMessage("Function Start")
        myActivityScope.launch {
            loggerClass.logMessage("Before Task")
            doLongRunningTask()
            loggerClass.logMessage("After Task")
        }
        loggerClass.logMessage("Function End")
    }
    /*
    Function Start
    Before Task
    Function End
    Before Delay
    After Delay
    After Task
    */

    fun launchInsideLifecycleScope() {
        loggerClass.logMessage("Function Start")
        viewModelScope.launch {
            loggerClass.logMessage("Before Task")
            launch(Dispatchers.Default) {
                loggerClass.logMessage("Before Delay")
                delay(2000)
                loggerClass.logMessage("After Delay")
            }
            loggerClass.logMessage("After Task")
        }
        loggerClass.logMessage("Function End")
    }
    /*
    Function Start
    Function End
    Before Task
    Before Delay
    After Delay
    After Task
    */

    fun twoLaunches() {
        loggerClass.logMessage("Function Start")

        viewModelScope.launch(Dispatchers.Default) {
            loggerClass.logMessage("Before Delay 1")
            delay(2000)
            loggerClass.logMessage("After Delay 1")
        }

        viewModelScope.launch(Dispatchers.Default) {
            loggerClass.logMessage("Before Delay 2")
            delay(2000)
            loggerClass.logMessage("After Delay 2")
        }
        loggerClass.logMessage("Function End")
    }
    /*
    Function Start
    Function End
    Before Delay 1
    Before Delay 2
    After Delay 1
    After Delay 2
    */

    fun twoAsyncInsideLifecycleScope() {
        loggerClass.logMessage("Function Start")
        viewModelScope.launch {
            loggerClass.logMessage("Before Task 1")
            val resultOne = doLongRunningTaskOne()
            loggerClass.logMessage("After Task 1")
            loggerClass.logMessage("Before Task 2")
            val resultTwo = doLongRunningTaskTwo()
            loggerClass.logMessage("After Task 2")
            val result = resultOne + resultTwo
            loggerClass.logMessage("result : $result")
        }
        loggerClass.logMessage("Function End")
    }

    fun twoWithContextInsideLifecycleScope() {
        loggerClass.logMessage("Function Start")

        viewModelScope.launch {
            loggerClass.logMessage("Before Task")

            val deferredOne = async {
                doLongRunningTaskOne()
            }

            val deferredTwo = async {
                doLongRunningTaskTwo()
            }

            val result = deferredOne.await() + deferredTwo.await()

            loggerClass.logMessage("result : $result")

            loggerClass.logMessage("After Task")
        }

        loggerClass.logMessage("Function End")
    }

    fun twoTasks() {
        Log.d(TAG, "Function Start")

        val job = viewModelScope.launch(Dispatchers.Main) {
            Log.d(TAG, "Before Task 1")
            doLongRunningTask()
            Log.d(TAG, "After Task 1")
        }

        viewModelScope.launch(Dispatchers.Main) {
            Log.d(TAG, "Before Task 2")
            job.cancel()
            doLongRunningTask()
            Log.d(TAG, "After Task 2")
        }

        Log.d(TAG, "Function End")
    }
/*
Function Start
Function End
Before Task 1
Before Task 2
Before Delay
Before Delay
After Delay
After Task 2
*/

    fun parentAndChildTaskCancel() {
        Log.d(TAG, "Function Start")

        viewModelScope.launch(Dispatchers.Main) {
            Log.d(TAG, "Before Task")
            childTask(coroutineContext[Job]!!)
            Log.d(TAG, "After Task")
        }

        Log.d(TAG, "Function End")
    }
/*
Function Start
Function End
Before Task
childTask start
childTask parent cancel
*/

    private suspend fun childTask(parent: Job) {
        withContext(Dispatchers.Default) {
            Log.d(TAG, "childTask start")
            parent.cancel()
            Log.d(TAG, "childTask parent cancel")
            // your code for doing a long running task
            // Added delay to simulate
            delay(2000)
            Log.d(TAG, "childTask end")
        }
    }

    fun parentAndChildTaskCancelIsActive() {
        Log.d(TAG, "Function Start")

        viewModelScope.launch(Dispatchers.Main) {
            Log.d(TAG, "Before Task")
            childTaskWithIsActive(coroutineContext[Job]!!)
            Log.d(TAG, "After Task")
        }

        Log.d(TAG, "Function End")
    }
/*
Function Start
Function End
Before Task
childTask start
*/

    private suspend fun childTaskWithIsActive(parent: Job) {
        withContext(Dispatchers.Default) {
            Log.d(TAG, "childTask start")
            parent.cancel()
            if (isActive) {
                Log.d(TAG, "childTask parent cancel")
            }
            // your code for doing a long running task
            // Added delay to simulate
            delay(2000)
            Log.d(TAG, "childTask end")
        }
    }

    private val exceptionHandler1 = CoroutineExceptionHandler { _, e ->
        Log.d(TAG, "exception handler: $e")
    }

    private fun lifecycleScopeWithHandlerException() {
        Log.d(TAG, "Function Start")
        viewModelScope.launch(exceptionHandler) {
            Log.d(TAG, "Before Task")
            doLongRunningTask()
            throw Exception("Some Exception")
            Log.d(TAG, "After Task")
        }
        Log.d(TAG, "Function End")
    }

    fun lifecycleScopeWithHandler() {
        Log.d(TAG, "Function Start")
        viewModelScope.launch(exceptionHandler) {
            Log.d(TAG, "Before Task")
            doLongRunningTask()
            Log.d(TAG, "After Task")
        }
        Log.d(TAG, "Function End")
    }

    fun myActivityScopeWithHandlerException() {
        Log.d(TAG, "Function Start")
        myActivityScope.launch(exceptionHandler) {
            Log.d(TAG, "Before Task")
            doLongRunningTask()
            throw Exception("Some Exception")
            Log.d(TAG, "After Task")
        }
        Log.d(TAG, "Function End")
    }
/*
Function Start
Function End
Before Task
childTask start
*/

    fun myActivityScopeWithHandler() {
        Log.d(TAG, "Function Start")
        myActivityScope.launch(exceptionHandler) {
            Log.d(TAG, "Before Task")
            doLongRunningTask()
            Log.d(TAG, "After Task")
        }
        Log.d(TAG, "Function End")
    }

    fun exceptionInLaunchBlock() {
        viewModelScope.launch {
            doSomethingAndThrowException()
        }
    }

    fun exceptionInAsyncBlock() {
        viewModelScope.async {
            doSomethingAndThrowException()
        }
    }

    fun exceptionInAsyncBlockWithAwait() {
        viewModelScope.launch {
            val deferred = viewModelScope.async(Dispatchers.Default) {
                doSomethingAndThrowException()
                return@async 10
            }
            try {
                val result = deferred.await()
            } catch (e: Exception) {
                Log.d(TAG, "exception handler: $e")
            }
        }
    }

    private fun doSomethingAndThrowException() {
        throw Exception("Some Exception")
    }

    private suspend fun timeTakingTask() {
        withContext(Dispatchers.IO) {
            // your code for doing a time taking task
            // Added delay to simulate
            Thread.sleep(5000)
        }
    }

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    fun testSuspending() {
        viewModelScope.launch(dispatcher) {
            Log.d(TAG, "testSuspending Before Task 1")
            timeTakingTask()
            Log.d(TAG, "testSuspending After Task 1")
        }
        viewModelScope.launch(dispatcher) {
            Log.d(TAG, "testSuspending Before Task 2")
            timeTakingTask()
            Log.d(TAG, "testSuspending After Task 2")
        }
    }

    fun testBlocking() {
        viewModelScope.launch(dispatcher) {
            runBlocking {
                Log.d(TAG, "testBlocking Before Task 1")
                timeTakingTask()
                Log.d(TAG, "testBlocking After Task 1")
            }
        }
        viewModelScope.launch(dispatcher) {
            runBlocking {
                Log.d(TAG, "testBlocking Before Task 2")
                timeTakingTask()
                Log.d(TAG, "testBlocking After Task 2")
            }
        }
    }

    private fun learnLaunch() {
        viewModelScope.launch(Dispatchers.Default) {
            // do something
        }
    }

    private fun learnLaunchWithJob() {
        val job = viewModelScope.launch(Dispatchers.Default) {
            // do something
        }
    }

    private suspend fun learnAsync() {
        val deferred = viewModelScope.async(Dispatchers.Default) {
            // do something
            return@async 10
        }
        val result = deferred.await()
    }

    private suspend fun asyncWithReturn(): Int {
        return viewModelScope.async(Dispatchers.Default) {
            // do something
            return@async 10
        }.await()
    }

    private suspend fun learnWithContext() {
        val result = withContext(Dispatchers.Default) {
            // do something
            return@withContext 10
        }
    }

    private suspend fun withContextWithReturn(): Int {
        return withContext(Dispatchers.Default) {
            // do something
            return@withContext 10
        }
    }


}
