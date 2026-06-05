package com.vibhorpatil.allproject.learnkotlinflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vibhorpatil.allproject.dependencyinjection.di.ActivityScope
import com.vibhorpatil.allproject.learnkotlincoroutine.LoggerClass
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice.FlowApiHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScope
class FlowViewModel @Inject constructor(
    private val flowApiHelper: FlowApiHelper,
    private val loggerClass: LoggerClass,
    private val dispatcherProvider: DefaultDispatcherProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState<List<ApiUser>>>(UIState.Loading)

    val uiState: StateFlow<UIState<List<ApiUser>>> = _uiState


    fun startSingleNetworkCallActivity() {
        viewModelScope.launch(dispatcherProvider.main) {
            _uiState.value = UIState.Loading
            flowApiHelper.getUsers()
                .flowOn(dispatcherProvider.io)
                .catch { e ->
                    _uiState.value = UIState.Error(e.toString())
                }.collect {
                    loggerClass.logFlowData(it)
                }

        }
    }
}