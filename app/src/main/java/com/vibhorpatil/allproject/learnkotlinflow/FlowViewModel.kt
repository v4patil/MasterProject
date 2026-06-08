package com.vibhorpatil.allproject.learnkotlinflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibhorpatil.allproject.dependencyinjection.di.ActivityScope
import com.vibhorpatil.allproject.learnkotlincoroutine.LoggerClass
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice.FlowApiHelper
import com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.UnknownServiceException
import javax.inject.Inject

@ActivityScope
class FlowViewModel @Inject constructor(
    private val flowApiHelper: FlowApiHelper,
    private val loggerClass: LoggerClass,
    private val dispatcherProvider: DefaultDispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState<List<ApiUser>>>(UIState.Loading)

    val uiState: StateFlow<UIState<List<ApiUser>>> = _uiState

    private val _uiStateStr = MutableStateFlow<UIState<String>>(UIState.Loading)

    val uiStateStr: StateFlow<UIState<String>> = _uiStateStr

    private val _uiStateUser = MutableStateFlow<UIState<List<User>>>(UIState.Loading)

    val uiStateUser: StateFlow<UIState<List<User>>> = _uiStateUser


    fun startSingleNetworkCallActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiState.value = UIState.Loading
            flowApiHelper.getUsers()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    _uiState.value = UIState.Error(e.toString())
                }.collect {
                    _uiState.value = UIState.Success(it)
                }

        }
    }

    fun startSeriesNetworkCallsActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiState.value = UIState.Loading
            val allUserList = mutableListOf<ApiUser>()
            flowApiHelper.getUsers()
                .flatMapConcat { userFromApi ->
                    allUserList.addAll(userFromApi)
                    flowApiHelper.getMoreUsers()
                }.flowOn(dispatcherProvider.default)
                .catch { e ->
                    _uiState.value = UIState.Error(e.toString())
                }.collect { moreUsersFromApi ->
                    allUserList.addAll(moreUsersFromApi)
                    _uiState.value = UIState.Success(allUserList)
                }
        }
    }

    fun startParallelNetworkCallsActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiState.value = UIState.Loading
            flowApiHelper.getUsers()
                .zip(flowApiHelper.getMoreUsers()) { userFromApi, moreUsersFromApi ->
                    val allUserList = mutableListOf<ApiUser>()
                    allUserList.addAll(userFromApi)
                    allUserList.addAll(moreUsersFromApi)
                    return@zip allUserList
                }.flowOn(dispatcherProvider.io)
                .catch { e ->
                    _uiState.value = UIState.Error(e.toString())
                }.collect {
                    _uiState.value = UIState.Success(it)
                }
        }
    }

    fun startCatchActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiState.value = UIState.Loading
            flowApiHelper.getUsersWithError()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    _uiState.value = UIState.Error(e.toString())
                }.collect {
                    _uiState.value = UIState.Success(it)
                }
        }
    }

    fun startEmitAllActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiState.value = UIState.Loading
            flowApiHelper.getUsers()
                .catch { e ->////individual catch block for network call
                    _uiState.value = UIState.Error(e.toString())
                }.zip(
                    flowApiHelper.getMoreUsers()
                        .catch { e ->//individual catch block for network call
                            _uiState.value = UIState.Error(e.toString())
                        }) { users, moreUsers ->
                    val allUserList = mutableListOf<ApiUser>()
                    allUserList.addAll(users)
                    allUserList.addAll(moreUsers)
                    return@zip allUserList
                }.flowOn(dispatcherProvider.default)
                .catch { e ->
                    _uiState.value = UIState.Error(e.toString())
                }
                .collect {
                    _uiState.value = UIState.Success(it)
                }
        }
    }

    fun startCompletionActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiState.value = UIState.Loading
            flowApiHelper.getUsers()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    _uiState.value = UIState.Error(e.toString())
                }
                .onCompletion {
                    _uiStateStr.value = UIState.Success("Task Completed")
                }
                .collect {
                }
        }
    }

    fun startLongRunningTaskActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiStateStr.value = UIState.Loading
            doLongRunningTask()
                .flowOn(dispatcherProvider.default)
                .catch { e ->
                    _uiStateStr.value = UIState.Error(e.toString())
                }.collect {
                    _uiStateStr.value = UIState.Success("Task Completed")
                }
        }
    }

    fun startTwoLongRunningTasksActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiStateStr.value = UIState.Loading
            doLongRunningTaskOne().zip(doLongRunningTaskTwo()) { resOne, resTwo ->
                return@zip resOne + resTwo
            }.catch { e ->
                _uiStateStr.value = UIState.Error(e.toString())
            }.collect {
                _uiStateStr.value = UIState.Success(it)
            }
        }
    }

    fun startFilterActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiStateStr.value = UIState.Loading
            val result = mutableListOf<Int>()
            (1..5).asFlow()
                .filter { it % 2 == 0 }.toList(result)
            _uiStateStr.value = UIState.Success(result.toString())
        }
    }

    fun startMapActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiStateUser.value = UIState.Loading
            flowApiHelper.getUsers()
                .map { apiUserList ->
                    val userList = mutableListOf<User>()
                    for (apiUser in apiUserList) {
                        val user = User(
                            id = apiUser.id,
                            name = apiUser.name,
                            email = apiUser.email,
                            avatar = apiUser.avatar
                        )
                        userList.add(user)
                    }
                    return@map userList
                }
                .flowOn(dispatcherProvider.default)
                .catch { e ->
                    _uiStateUser.value = UIState.Error(e.toString())
                }
                .collect {
                    _uiStateUser.value = UIState.Success(it)
                }
        }
    }

//    Difference between reduce() and fold()
//    reduce : Uses the first element as the initial accumulator.
//    fold : You provide the initial value.

//    Important interview point
//    reduce() throws an exception if the Flow is empty because it needs the first element as the starting accumulator.
//    Use fold() when you need to handle empty flows safely:

    fun startReduceActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiStateStr.value = UIState.Loading
            val result = (1..5).asFlow()
                .reduce { a, b -> a + b }

            _uiStateStr.value = UIState.Success(result.toString())
        }
    }

    fun startFoldActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiStateStr.value = UIState.Loading
            val result = (1..5).asFlow()
                .fold(100) { a, b -> a + b }

            _uiStateStr.value = UIState.Success(result.toString())
        }
    }

    fun startRetryActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiStateStr.value = UIState.Loading
            // do a long running task
            doLongRunningRetryTask()
                .flowOn(dispatcherProvider.default)
                .retry(retries = 3) { cause ->
                    if (cause is IOException) {
                        delay(2000)
                        return@retry true
                    } else {
                        return@retry false
                    }
                }
                .catch {
                    _uiStateStr.value = UIState.Error("Something Went Wrong")
                }
                .collect {
                    _uiStateStr.value = UIState.Success("Task Completed")
                }
        }
    }

    fun startRetryWhenActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiStateStr.value = UIState.Loading
            // do a long running task
            doLongRunningRetryWhenTask()
                .flowOn(dispatcherProvider.default)
                .retryWhen { cause, attempt ->
                    if (cause is IOException && attempt < 3) {
                        delay(2000)
                        return@retryWhen true
                    } else {
                        return@retryWhen false
                    }
                }
                .catch {
                    _uiStateStr.value = UIState.Error("Something Went Wrong")
                }
                .collect {
                    _uiStateStr.value = UIState.Success("Task Completed")
                }
        }
    }

    fun startRetryExponentialBackoffActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiStateStr.value = UIState.Loading
            // do a long running task
            var currentDelay = 1000L
            val delayFactor = 2
            doLongRunningBackOffTask()
                .flowOn(dispatcherProvider.default)
                .retry(retries = 3) { cause ->
                    if (cause is IOException) {
                        delay(currentDelay)
                        currentDelay = (currentDelay * delayFactor)
                        return@retry true
                    } else {
                        return@retry false
                    }
                }
                .catch {
                    _uiStateStr.value = UIState.Error("Something Went Wrong")
                }
                .collect {
                    _uiStateStr.value = UIState.Success("Task Completed")
                }
        }
    }

    fun startFlowOnActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            doLongRunningTask()
                .flowOn(dispatcherProvider.default)
                .filter {
                    printThreadName("filter 1")
                    return@filter true
                }
                .map {
                    printThreadName("map 1")
                    return@map it * it
                }
                .flowOn(dispatcherProvider.default)
                .filter {
                    printThreadName("filter 2")
                    return@filter true
                }
                .flowOn(dispatcherProvider.main)
                .map {
                    printThreadName("map 2")
                    return@map it * it
                }
                .flowOn(dispatcherProvider.default)
                .filter {
                    printThreadName("filter 3")
                    return@filter true
                }
                .map {
                    printThreadName("map 3")
                    return@map it * it
                }
                .collect {
                    printThreadName("collect")
                }
        }
    }

    private fun printThreadName(string: String) {
        loggerClass.logMessage(string)
    }

    private fun doLongRunningTask(): Flow<Int> {
        val c = 2 + 2
        loggerClass.logMessage("1 " + Thread.currentThread().name)//1 main
        return flow {
            loggerClass.logMessage("2 " + Thread.currentThread().name)//2 DefaultDispatcher-worker-1

            // your code for doing a long running task
            // Added delay to simulate
            delay(5000)
            emit(0)
        }
    }


    private fun doLongRunningTaskTwo(): Flow<String> {
        return flow {
            // your code for doing a long running task
            // Added delay to simulate
            delay(5000)
            emit("Two")
        }
    }

    private fun doLongRunningTaskOne(): Flow<String> {
        return flow {
            // your code for doing a long running task
            // Added delay to simulate
            delay(5000)
            emit("One")
        }
    }

    private fun doLongRunningRetryTask(): Flow<Int> {
        return flow {
            // your code for doing a long running task
            // Added delay, random number, and exception to simulate

            delay(2000)

            val randomNumber = (0..2).random()

            if (randomNumber == 0) {
                throw IOException()
            } else if (randomNumber == 1) {
                throw IndexOutOfBoundsException()
            }

            delay(2000)
            emit(0)
        }
    }

    private fun doLongRunningRetryWhenTask(): Flow<Int> {
        return flow {
            // your code for doing a long running task
            // Added delay, random number, and exception to simulate

            delay(2000)

            val randomNumber = (0..2).random()

            if (randomNumber == 0) {
                throw IOException()
            } else if (randomNumber == 1) {
                throw IndexOutOfBoundsException()
            }

            delay(2000)
            emit(0)
        }
    }

    private fun doLongRunningBackOffTask(): Flow<Int> {
        return flow {
            // your code for doing a long running task
            // Added delay, random number, and exception to simulate

            delay(2000)

            val randomNumber = (0..2).random()

            if (randomNumber == 0) {
                throw IOException()
            } else if (randomNumber == 1) {
                throw IndexOutOfBoundsException()
            }

            delay(2000)
            emit(0)
        }
    }


}