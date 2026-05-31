package com.vibhorpatil.allproject.dependencyinjection.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.vibhorpatil.allproject.AllProjectApplication
import com.vibhorpatil.allproject.databinding.ActivityLoginBinding
import com.vibhorpatil.allproject.dependencyinjection.data.local.DatabaseService
import com.vibhorpatil.allproject.dependencyinjection.data.remote.NetworkService
import com.vibhorpatil.allproject.dependencyinjection.di.component.ActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.component.DaggerActivityComponent
import com.vibhorpatil.allproject.dependencyinjection.di.module.ActivityModule
import com.vibhorpatil.allproject.dependencyinjection.di.module.ApplicationModule
import com.vibhorpatil.allproject.dependencyinjection.ui.home.HomeActivity
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var activityComponent: ActivityComponent

    @Inject
    lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var dbService: DatabaseService

    @Inject
    lateinit var networkService: NetworkService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDependencies()
        Log.d("TAG", "onCreate : ${viewModel.javaClass.name + " " + viewModel.hashCode()}")
        Log.d("TAG", "onCreate : ${dbService.javaClass.name + " " + dbService.hashCode()}")
        Log.d("TAG", "onCreate : ${networkService.javaClass.name + " " + networkService.hashCode()}")

        setClickListener()
    }

    private fun getDependencies() {
        activityComponent = DaggerActivityComponent
            .builder()
            .applicationComponent((application as AllProjectApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()
        activityComponent.inject(this)
    }

    fun setClickListener(){
        binding.btnHomeActivity.setOnClickListener{startActivity(Intent(this, HomeActivity::class.java))}
    }
}