package com.vibhorpatil.allproject.dependencyinjection.di.module

import android.app.Application
import com.vibhorpatil.allproject.dependencyinjection.data.local.DatabaseService
import com.vibhorpatil.allproject.dependencyinjection.data.remote.HttpClient
import com.vibhorpatil.allproject.dependencyinjection.di.ApplicationContext
import com.vibhorpatil.allproject.dependencyinjection.di.BaseURL
import com.vibhorpatil.allproject.dependencyinjection.di.DatabaseName
import com.vibhorpatil.allproject.dependencyinjection.di.DatabaseVersion
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiHelper
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.ApiHelperImpl
import com.vibhorpatil.allproject.learnkotlincoroutine.networkservice.RetrofitBuilder
import com.vibhorpatil.allproject.learnkotlinflow.DefaultDispatcherProvider
import com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice.FlowApiHelper
import com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice.FlowApiHelperImpl
import com.vibhorpatil.allproject.learnkotlinflow.flownetworkservice.FlowRetrofitBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(val application: Application) {


    @Provides
    @ApplicationContext
    fun provideContext(): Application {
        return application
    }

    @Singleton
    @Provides
    fun provideDatabaseService(
        @DatabaseName databaseName: String,
        @DatabaseVersion databaseVersion: Int
    ): DatabaseService {
        return DatabaseService(application, databaseName, databaseVersion)
    }

    @Provides
    @DatabaseName
    fun provideDatabaseName(): String = "DATABASE_NAME"

    @Provides
    @DatabaseVersion
    fun provideDatabaseVersion(): Int = 1

    @Provides
    @BaseURL
    fun provideBaseUrl(): String = "BASE_URL"

    @Provides
    @Singleton
    fun provideHttpClient() : HttpClient = HttpClient()

    @Provides
    @Singleton
    fun provideApiHelper() : ApiHelper = ApiHelperImpl(RetrofitBuilder.apiService)

    @Provides
    @Singleton
    fun providerDefaultDispatcherProvider(): DefaultDispatcherProvider = DefaultDispatcherProvider()

    @Provides
    @Singleton
    fun provideFlowApiHelper() : FlowApiHelper = FlowApiHelperImpl(FlowRetrofitBuilder.flowApiService)


}