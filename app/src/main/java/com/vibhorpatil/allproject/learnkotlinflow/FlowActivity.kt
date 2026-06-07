package com.vibhorpatil.allproject.learnkotlinflow

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vibhorpatil.allproject.AllProjectApplication
import com.vibhorpatil.allproject.databinding.ActivityFlowBinding
import com.vibhorpatil.allproject.dependencyinjection.di.component.ActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.component.DaggerActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.module.ActivityModule
import com.vibhorpatil.allproject.learnkotlincoroutine.LoggerClass
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class FlowActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    lateinit var binding: ActivityFlowBinding

    lateinit var activityComponent: ActivityComponent

    @Inject
    lateinit var viewModel: FlowViewModel

    @Inject
    lateinit var loggerClass: LoggerClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDependencies()
        setupObserver()

    }

    private fun getDependencies() {
        activityComponent = DaggerActivityComponent.builder()
            .applicationComponent((application as AllProjectApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()
        activityComponent.inject(this)
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    when (it) {
                        is UIState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is UIState.Success<*> -> {
                            binding.progressBar.visibility = View.GONE

                            val users = it.data as? List<ApiUser>
                            users?.let { list ->
                                loggerClass.logFlowData(list)
                            }
                        }

                        is UIState.Error -> {
                            loggerClass.logMessage(it.error)
                        }
                    }
                }
            }
        }
    }

    fun startSingleNetworkCallActivity(view: View) {
        viewModel.startSingleNetworkCallActivity()
    }

    fun startSeriesNetworkCallsActivity(view: View) {
        viewModel.startSeriesNetworkCallsActivity()
    }

    fun startParallelNetworkCallsActivity(view: View) {
        viewModel.startParallelNetworkCallsActivity()
    }

    fun startRoomDatabaseActivity(view: View) {
    }

    fun startCatchActivity(view: View) {
        viewModel.startCatchActivity()
    }

    fun startEmitAllActivity(view: View) {
        viewModel.startEmitAllActivity()
    }

    fun startCompletionActivity(view: View) {
        viewModel.startCompletionActivity()
    }

    fun startLongRunningTaskActivity(view: View) {
        viewModel.startLongRunningTaskActivity()
    }

    fun startTwoLongRunningTasksActivity(view: View) {
        viewModel.startTwoLongRunningTasksActivity()
    }

    fun startFilterActivity(view: View) {
        viewModel.startFilterActivity()
    }

    fun startMapActivity(view: View) {
        viewModel.startMapActivity()
    }

    fun startReduceActivity(view: View) {
        viewModel.startReduceActivity()
    }

    fun startSearchActivity(view: View) {
        setUpSearchStateFlow()
    }

    fun startRetryActivity(view: View) {
        viewModel.startRetryActivity()
    }

    fun startRetryWhenActivity(view: View) {
        viewModel.startRetryWhenActivity()
    }

    fun startRetryExponentialBackoffActivity(view: View) {
        viewModel.startRetryExponentialBackoffActivity()
    }

    fun startFlowOnActivity(view: View) {
        viewModel.startFlowOnActivity()
    }

    private fun setUpSearchStateFlow() {
        launch {
            binding.searchView.getQueryTextChangeStateFlow()
                .debounce(300)
                .filter { query ->
                    if (query.isEmpty()) {
                        binding.textViewResult.text = ""
                        return@filter false
                    } else {
                        return@filter true
                    }
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    dataFromNetwork(query)
                        .catch {
                            emitAll(flowOf(""))
                        }
                }
                .flowOn(DefaultDispatcherProvider().default)
                .collect { result ->
                    binding.textViewResult.text = result
                }
        }
    }

    /**
     * Simulation of network data
     */
    private fun dataFromNetwork(query: String): Flow<String> {
        return flow {
            delay(2000)
            emit(query)
        }
    }

}