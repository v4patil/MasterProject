package com.vibhorpatil.allproject.dependencyinjection.ui.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.vibhorpatil.allproject.AllProjectApplication
import com.vibhorpatil.allproject.R
import com.vibhorpatil.allproject.dependencyinjection.data.local.DatabaseService
import com.vibhorpatil.allproject.dependencyinjection.data.local.FileStorageService
import com.vibhorpatil.allproject.dependencyinjection.di.component.ActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.component.DaggerActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.module.ActivityModule
import com.vibhorpatil.allproject.dependencyinjection.ui.login.LoginViewModel
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

    lateinit var activityComponent: ActivityComponent

    @Inject
    lateinit var homeViewModel: HomeViewModel

    @Inject
    lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var dbService: DatabaseService

    @Inject
    lateinit var fileStorageService: FileStorageService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getDependencies()
        Log.d("TAG", "onCreate : ${homeViewModel.javaClass.name + " " + homeViewModel.hashCode()}")
        Log.d("TAG", "onCreate : ${loginViewModel.javaClass.name + " " + loginViewModel.hashCode()}")
        Log.d("TAG", "onCreate : ${dbService.javaClass.name + " " + dbService.hashCode()}")
        Log.d("TAG", "onCreate : ${fileStorageService.javaClass.name + " " + fileStorageService.hashCode()}")
    }

    private fun getDependencies() {
        activityComponent = DaggerActivityComponent
            .builder()
            .applicationComponent((application as AllProjectApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()
        activityComponent.inject(this)
    }
}