package com.vibhorpatil.allproject.learnkotlincoroutine

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vibhorpatil.allproject.AllProjectApplication
import com.vibhorpatil.allproject.databinding.ActivityCoroutineBinding
import com.vibhorpatil.allproject.dependencyinjection.di.component.ActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.component.DaggerActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.module.ActivityModule
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoroutineActivity : AppCompatActivity() {
    lateinit var binding: ActivityCoroutineBinding
    lateinit var activityComponent: ActivityComponent
    @Inject
    lateinit var coroutineViewModel: CoroutineViewModel
    @Inject
    lateinit var loggerClass: LoggerClass
    lateinit var exceptionHandler : CoroutineExceptionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDependencies()
        exceptionHandler = CoroutineExceptionHandler{ _,e ->
            loggerClass.logMessage("Exception caught: ${e.message}")
        }
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

    /**************************** Below are the coroutine basics ***********************************************/

    fun startBasicActivity(view: View) {
        Toast.makeText(this, "Below are the coroutine Basics", Toast.LENGTH_SHORT).show()
    }

    fun testCoroutine(view: View) {
        coroutineViewModel.testCoroutine()
    }

    fun testCoroutineWithMain(view: View) {
        coroutineViewModel.testCoroutineWithMain()
    }

    fun testCoroutineWithMainImmediate(view: View) {
        coroutineViewModel.testCoroutineWithMainImmediate()
    }

    fun testCoroutineEverything(view: View) {
        coroutineViewModel.testCoroutineEverything()
    }

    fun usingGlobalScope(view: View) {
        coroutineViewModel.usingGlobalScope()
    }

    fun globalScopeInsideLifecycleScope(view: View) {
        globalScopeInsideLifecycleScope()
    }

    fun globalScopeInsideLifecycleScope() {
        loggerClass.logMessage("Function Start")
        lifecycleScope.launch {
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
/*
Function Start
Before Task
After Task
Function End
Before Delay
After Delay
*/

    fun launchInsideLifecycleScope(view: View) {
        launchInsideLifecycleScope()
    }

    fun launchInsideLifecycleScope() {
        loggerClass.logMessage("Function Start")
        lifecycleScope.launch {
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
    Before Task
    After Task
    Function End
    Before Delay
    After Delay

    */

    fun twoLaunches(view: View) {
        coroutineViewModel.twoLaunches()
    }

    fun twoAsyncInsideLifecycleScope(view: View) {
        twoAsyncInsideLifecycleScope()
    }

    fun twoAsyncInsideLifecycleScope() {
        loggerClass.logMessage("Function Start")
        lifecycleScope.launch {
            loggerClass.logMessage("Before Task 1")
            val resultOne = coroutineViewModel.doLongRunningTaskOne()
            loggerClass.logMessage("After Task 1")
            loggerClass.logMessage("Before Task 2")
            val resultTwo = coroutineViewModel.doLongRunningTaskTwo()
            loggerClass.logMessage("After Task 2")
            val result = resultOne + resultTwo
            loggerClass.logMessage("result : $result")
        }
        loggerClass.logMessage("Function End")
    }
    /*
    Function Start
    Before Task 1
    Function End
    After Task 1
    Before Task 2
    After Task 2
    20
    */

    fun twoWithContextInsideLifecycleScope(view: View) {
        twoWithContextInsideLifecycleScope()
    }

    fun twoWithContextInsideLifecycleScope() {
        loggerClass.logMessage("Function Start")

        lifecycleScope.launch {
            loggerClass.logMessage("Before Task")

            val deferredOne = async {
                coroutineViewModel.doLongRunningTaskOne()
            }

            val deferredTwo = async {
                coroutineViewModel.doLongRunningTaskTwo()
            }

            val result = deferredOne.await() + deferredTwo.await()

            loggerClass.logMessage("result : $result")

            loggerClass.logMessage("After Task")
        }

        loggerClass.logMessage("Function End")
    }
/*
Function Start
Before Task
Function End
20
After Task
*/

    fun twoTasks(view: View) {
        coroutineViewModel.twoTasks()
    }

    fun usingMyActivityScope(view: View) {
        coroutineViewModel.usingMyActivityScope()
    }

    fun parentAndChildTaskCancel(view: View) {
        coroutineViewModel.parentAndChildTaskCancel()
    }

    fun parentAndChildTaskCancelIsActive(view: View) {
        coroutineViewModel.parentAndChildTaskCancelIsActive()
    }

    fun lifecycleScopeWithHandlerException(view: View) {
        lifecycleScopeWithHandlerException()
    }

    private fun lifecycleScopeWithHandlerException() {
        loggerClass.logMessage("Function Start")
        lifecycleScope.launch(exceptionHandler) {
            loggerClass.logMessage("Before Task")
            doLongRunningTask()
            throw Exception("Some Exception")
            loggerClass.logMessage("After Task")
        }
        loggerClass.logMessage("Function End")
    }
/*
Function Start
Before Task
Function End
Exception caught: Some Exception
*/

    fun lifecycleScopeWithHandler(view: View) {
        lifecycleScopeWithHandler()
    }

    fun lifecycleScopeWithHandler() {
        loggerClass.logMessage("Function Start")
        lifecycleScope.launch(exceptionHandler) {
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
After Task
*/


    fun myActivityScopeWithHandlerException(view: View) {
        coroutineViewModel.myActivityScopeWithHandlerException()
    }

    fun myActivityScopeWithHandler(view: View) {
        coroutineViewModel.myActivityScopeWithHandler()
    }

    fun exceptionInLaunchBlock(view: View) {
        coroutineViewModel.exceptionInLaunchBlock()
    }

    fun exceptionInAsyncBlock(view: View) {
        coroutineViewModel.exceptionInAsyncBlock()
    }

    fun exceptionInAsyncBlockWithAwait(view: View) {
        coroutineViewModel.exceptionInAsyncBlockWithAwait()
    }

    fun testSuspending(view: View) {
        coroutineViewModel.testSuspending()
    }

    fun testBlocking(view: View) {
        coroutineViewModel.testBlocking()
    }


}