package com.vibhorpatil.allproject.dependencyinjection.di.component

import com.vibhorpatil.allproject.AllProjectApplication
import com.vibhorpatil.allproject.dependencyinjection.data.local.DatabaseService
import com.vibhorpatil.allproject.dependencyinjection.data.local.FileStorageService
import com.vibhorpatil.allproject.dependencyinjection.data.remote.NetworkService
import com.vibhorpatil.allproject.dependencyinjection.di.module.ApplicationModule
import com.vibhorpatil.allproject.learnkotlincoroutine.LoggerClass
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiHelper
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(application: AllProjectApplication)

    fun getDatabaseService() : DatabaseService

    fun getFileStorageService() : FileStorageService

    fun getNetworkService() : NetworkService

    fun getApiHelper() : ApiHelper

    fun getLoggerClass() : LoggerClass
}