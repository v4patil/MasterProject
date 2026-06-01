package com.vibhorpatil.allproject.learnkotlincoroutine

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vibhorpatil.allproject.AllProjectApplication
import com.vibhorpatil.allproject.databinding.ActivityCoroutineBinding
import com.vibhorpatil.allproject.dependencyinjection.di.component.ActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.component.DaggerActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.module.ActivityModule
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
//    fun startTimeoutActivity(view: View) {
//        startActivity(Intent(this@CoroutineActivity, TimeoutActivity::class.java))
//    }
//
//    fun startTryCatchActivity(view: View) {
//        startActivity(Intent(this@CoroutineActivity, TryCatchActivity::class.java))
//    }
//
//    fun startExceptionHandlerActivity(view: View) {
//        startActivity(Intent(this@CoroutineActivity, ExceptionHandlerActivity::class.java))
//    }
//
//    fun startIgnoreErrorAndContinueActivity(view: View) {
//        startActivity(Intent(this@CoroutineActivity, IgnoreErrorAndContinueActivity::class.java))
//    }
//
//    fun startLongRunningTaskActivity(view: View) {
//        startActivity(Intent(this@CoroutineActivity, LongRunningTaskActivity::class.java))
//    }
//
//    fun startTwoLongRunningTasksActivity(view: View) {
//        startActivity(Intent(this@CoroutineActivity, TwoLongRunningTasksActivity::class.java))
//    }
//
//    fun startBasicActivity(view: View) {
//        startActivity(Intent(this@CoroutineActivity, BasicActivity::class.java))
//    }
}