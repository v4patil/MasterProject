package com.vibhorpatil.allproject.learnkotlincoroutine

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vibhorpatil.allproject.AllProjectApplication
import com.vibhorpatil.allproject.databinding.ActivityCoroutineBinding
import com.vibhorpatil.allproject.dependencyinjection.di.component.ActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.component.DaggerActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.module.ActivityModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoroutineActivity : AppCompatActivity() {
    lateinit var binding: ActivityCoroutineBinding
    lateinit var activityComponent: ActivityComponent
    @Inject
    lateinit var coroutineViewModel: CoroutineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDependencies()
    }

    private fun getDependencies() {
       activityComponent = DaggerActivityComponent
           .builder()
           .applicationComponent((application as AllProjectApplication).applicationComponent)
           .activityModule(ActivityModule(this))
           .build()
        activityComponent.inject(this)
    }

    fun startSingleNetworkCallActivity(view: View) {
        coroutineViewModel.doSingleNetworkCall()
    }

    fun startSeriesNetworkCallsActivity(view: View) {
        coroutineViewModel.doSeriesNetworkCall()
    }

    fun startParallelNetworkCallsActivity(view: View) {
        coroutineViewModel.doParallelNetworkCall()
    }
//
//    fun startRoomDatabaseActivity(view: View) {
//        startActivity(Intent(this@CoroutineActivity, RoomDBActivity::class.java))
//    }
//
    fun startTimeoutActivity(view: View) {
        coroutineViewModel.doWithTimeOut()
    }

    fun startTimeoutOrNullActivity(view: View) {
        coroutineViewModel.doWithTimeOutOrNull()
    }

    fun startTryCatchActivity(view: View) {
        coroutineViewModel.doWithTryCatch()
    }

    fun startExceptionHandlerActivity(view: View) {
        coroutineViewModel.doWithExceptionHandler()
    }

    fun startIgnoreErrorAndContinueActivity(view: View) {
       coroutineViewModel.doIgnoreErrorAndContinue()
    }

    private suspend fun doLongRunningTask() {
        withContext(Dispatchers.Default) {
            // your code for doing a long running task
            // Added delay to simulate
            delay(5000)
        }
    }

    fun startLongRunningTaskActivity(view: View) {
        coroutineViewModel.doSingleLongRunningTask()
    }

    fun startTwoLongRunningTasksActivity(view: View) {
        coroutineViewModel.doTwoLongRunningTask()
    }

    /** Below are the coroutine basics **/

    fun startBasicActivity(view: View) {
        Toast.makeText(this, "Below are the coroutine Baiscs", Toast.LENGTH_SHORT).show()
    }
}