package com.vibhorpatil.allproject.dependencyinjection.di.component

import com.vibhorpatil.allproject.AllProjectApplication
import com.vibhorpatil.allproject.dependencyinjection.data.local.DatabaseService
import com.vibhorpatil.allproject.dependencyinjection.data.local.FileStorageService
import com.vibhorpatil.allproject.dependencyinjection.data.remote.NetworkService
import com.vibhorpatil.allproject.dependencyinjection.di.module.ApplicationModule
import com.vibhorpatil.allproject.learnkotlincoroutine.LoggerClass
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiHelper
import com.vibhorpatil.allproject.learnkotlinflow.DefaultDispatcherProvider
import com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice.FlowApiHelper
import com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice.FlowApiHelperImpl
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

    fun getDefaultDispatcherProvider() : DefaultDispatcherProvider

    fun getFlowApiHelper() : FlowApiHelper
}