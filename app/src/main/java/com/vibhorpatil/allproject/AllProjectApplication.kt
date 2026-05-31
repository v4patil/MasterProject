package com.vibhorpatil.allproject

import android.app.Application
import com.vibhorpatil.allproject.dependencyinjection.di.component.ApplicationComponent
import com.vibhorpatil.allproject.dependencyinjection.di.component.DaggerApplicationComponent
import com.vibhorpatil.allproject.dependencyinjection.di.module.ApplicationModule

class AllProjectApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        getDependencies()
    }

    private fun getDependencies() {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this)).build()
        applicationComponent.inject(this)

    }
}