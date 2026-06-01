package com.vibhorpatil.allproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vibhorpatil.allproject.databinding.ActivityMainBinding
import com.vibhorpatil.allproject.dependencyinjection.ui.login.LoginActivity
import com.vibhorpatil.allproject.learnkotlincoroutine.CoroutineActivity

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())
        setClickListener()
    }

    fun setClickListener(){
        binding.btnDagger.setOnClickListener{navigateTo(LoginActivity::class.java)}
        binding.btnCoroutine.setOnClickListener{navigateTo(CoroutineActivity::class.java)}
    }

    fun navigateTo(clazz : Class<*>){
        val intent = Intent(this, clazz)
        startActivity(intent)
    }
}