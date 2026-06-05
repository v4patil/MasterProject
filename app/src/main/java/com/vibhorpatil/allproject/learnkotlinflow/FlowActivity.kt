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
import kotlinx.coroutines.launch
import javax.inject.Inject

class FlowActivity : AppCompatActivity() {
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
        activityComponent = DaggerActivityComponent.builder().
                applicationComponent((application as AllProjectApplication).applicationComponent)
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
                            users?.let{ list ->
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

//    fun startSeriesNetworkCallsActivity(view: View) {
//        startActivity(Intent(this@MainActivity, SeriesNetworkCallsActivity::class.java))
//    }
//
//    fun startParallelNetworkCallsActivity(view: View) {
//        startActivity(Intent(this@MainActivity, ParallelNetworkCallsActivity::class.java))
//    }
//
//    fun startRoomDatabaseActivity(view: View) {
//        startActivity(Intent(this@MainActivity, RoomDBActivity::class.java))
//    }
//
//    fun startCatchActivity(
//        view: View) {
//        startActivity(Intent(this@MainActivity, CatchActivity::class.java))
//    }
//
//    fun startEmitAllActivity(view: View) {
//        startActivity(Intent(this@MainActivity, EmitAllActivity::class.java))
//    }
//
//    fun startCompletionActivity(view: View) {
//        startActivity(Intent(this@MainActivity, CompletionActivity::class.java))
//    }
//
//    fun startLongRunningTaskActivity(view: View) {
//        startActivity(Intent(this@MainActivity, LongRunningTaskActivity::class.java))
//    }
//
//    fun startTwoLongRunningTasksActivity(view: View) {
//        startActivity(Intent(this@MainActivity, TwoLongRunningTasksActivity::class.java))
//    }
//
//    fun startFilterActivity(view: View) {
//        startActivity(Intent(this@MainActivity, FilterActivity::class.java))
//    }
//
//    fun startMapActivity(view: View) {
//        startActivity(Intent(this@MainActivity, MapActivity::class.java))
//    }
//
//    fun startReduceActivity(view: View) {
//        startActivity(Intent(this@MainActivity, ReduceActivity::class.java))
//    }
//
//    fun startSearchActivity(view: View) {
//        startActivity(Intent(this@MainActivity, SearchActivity::class.java))
//    }
//
//    fun startRetryActivity(view: View) {
//        startActivity(Intent(this@MainActivity, RetryActivity::class.java))
//    }
//
//    fun startRetryWhenActivity(view: View) {
//        startActivity(Intent(this@MainActivity, RetryWhenActivity::class.java))
//    }
//
//    fun startRetryExponentialBackoffActivity(view: View) {
//        startActivity(Intent(this@MainActivity, RetryExponentialBackoffActivity::class.java))
//    }
//
//    fun startFlowOnActivity(view: View) {
//        startActivity(Intent(this@MainActivity, FlowOnActivity::class.java))
//    }


}